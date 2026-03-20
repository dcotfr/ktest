package ktest.kafka;

import com.fasterxml.jackson.databind.node.TextNode;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.domain.TestRecord;
import ktest.json.JsonAssert;
import ktest.kafka.avrogen.JsonAvroConverter;
import org.apache.kafka.clients.consumer.CloseOptions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RegisterForReflection(registerFullHierarchy = true)
@ApplicationScoped
public class ClusterClient {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterClient.class);

    private final Map<String, KafkaProducer<Object, Object>> producers = new HashMap<>();
    private final Map<String, KafkaConsumer<Object, Object>> consumers = new HashMap<>();
    private final KafkaConfigProvider kafkaConfigProvider;
    private final RegistryService registryService;
    private final JsonAvroConverter jsonAvroConverter;

    @Inject
    public ClusterClient(final KafkaConfigProvider pKafkaConfigProvider, final RegistryService pRegistryService,
                         final JsonAvroConverter pJsonAvroConverter) {
        kafkaConfigProvider = pKafkaConfigProvider;
        registryService = pRegistryService;
        jsonAvroConverter = pJsonAvroConverter;
    }

    @PreDestroy
    public void destroy() {
        consumers.values().forEach(c -> c.close(CloseOptions.timeout(Duration.ofSeconds(5))));
        producers.values().forEach(p -> p.close(Duration.ofSeconds(5)));
    }

    public void send(final String pLogPrefix, final TopicRef pTopic, final TestRecord pRecord, final String pForcedKeySchema, final String pForcedValueSchema) {
        final var producer = producer(pLogPrefix, pTopic);
        LOG.trace("{}  Sending record to {}.", pLogPrefix, pTopic.id());

        final var rec = new ProducerRecord<>(pTopic.topic(),
                null,
                pRecord.longTimestamp(),
                convert(pLogPrefix, pTopic, pRecord, true, pForcedKeySchema),
                convert(pLogPrefix, pTopic, pRecord, false, pForcedValueSchema),
                kafkaHeaders(pRecord.headers()));
        try {
            CustomSubjectNameStrategy.define(pTopic.topic(), pForcedKeySchema, pForcedValueSchema);
            final var futur = producer.send(rec);
            producer.flush();
            futur.get(30, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            throw new KTestException("Failed to send record to " + pTopic.id(), e);
        } catch (final InterruptedException e) {
            LOG.error("{}Interrupted!", pLogPrefix, e);
            Thread.currentThread().interrupt();
        }
    }

    @Retry(retryOn = SocketTimeoutException.class)
    public FoundRecord find(final String pLogPrefix, final TopicRef pTopic, final TestRecord pRecord, final int pBackOffset) {
        final var consumer = consumer(pLogPrefix, pTopic);
        synchronized (consumer) {
            final var searchRange = resetConsumer(pLogPrefix, consumer, pTopic.topic(), pBackOffset);
            while (searchRange.hasNext()) {
                final var remainingPartitions = searchRange.partitionsHavingNext();
                if (remainingPartitions.isEmpty()) {
                    return null;
                }
                consumer.assign(remainingPartitions);
                final var recs = consumer.poll(Duration.ofMillis(5000));
                LOG.trace("{}  Comparing with {} records from topic {}.", pLogPrefix, recs.count(), pTopic.id());
                consumer.commitSync();
                if (recs.isEmpty()) {
                    return null;
                }
                for (final var o : recs) {
                    if (o instanceof final ConsumerRecord<?, ?> rec) {
                        searchRange.currentOffset(rec.partition(), rec.offset());
                        if (assertRecord(pRecord, rec)) {
                            return new FoundRecord(rec);
                        }
                    }
                }
            }
        }
        return null;
    }

    public TopicRef scanSerdes(final String pLogPrefix, final String pBroker, final String pTopic) {
        final var temporaryTopicRef = new TopicRef(pBroker, pTopic, Serde.BYTES, Serde.BYTES);
        final var keySerde = registryService.lastActiveSchema(pLogPrefix, temporaryTopicRef, true, null) != null ? Serde.AVRO : Serde.STRING;
        final var valueSerde = registryService.lastActiveSchema(pLogPrefix, temporaryTopicRef, false, null) != null ? Serde.AVRO : Serde.STRING;
        kafkaConfigProvider.reset();
        return new TopicRef(pBroker, pTopic, keySerde, valueSerde);
    }

    private SearchRange resetConsumer(final String pLogPrefix, final KafkaConsumer<?, ?> pConsumer, final String pTopicName, final int pBackOffset) {
        LOG.trace("{}  Start of reset of consumer from {}.", pLogPrefix, pTopicName);
        final var partitions = pConsumer.partitionsFor(pTopicName).stream()
                .map(p -> new TopicPartition(pTopicName, p.partition())).toList();
        final var res = new SearchRange(pTopicName);
        pConsumer.assign(partitions);
        for (final var e : pConsumer.endOffsets(partitions).entrySet()) {
            final var endOffset = e.getValue();
            final var startOffset = Math.max(endOffset - pBackOffset - 1, 0);
            res.addRange(e.getKey().partition(), startOffset, endOffset - 1);
            pConsumer.seek(e.getKey(), startOffset);
        }
        LOG.trace("{}  End of reset of consumer from {}.", pLogPrefix, pTopicName);
        return res;
    }

    private boolean assertRecord(final TestRecord pExpected, final ConsumerRecord<?, ?> pActual) {
        if (pExpected.longTimestamp() != null && pExpected.longTimestamp() != pActual.timestamp()) {
            return false;
        }
        for (final var h : pExpected.headers().entrySet()) {
            final var actuelHeader = pActual.headers().lastHeader(h.getKey());
            if (actuelHeader == null || !Objects.equals(h.getValue(), new String(actuelHeader.value(), StandardCharsets.UTF_8))) {
                return false;
            }
        }
        final var expectedKey = pExpected.keyNode();
        if (expectedKey != null) {
            final var actualKey = pActual.key();
            if (actualKey == null || !JsonAssert.contains(expectedKey.toString(), actualKey.toString()).isEmpty()) {
                return false;
            }
        }
        final var expectedValue = pExpected.valueNode();
        if (expectedValue != null) {
            final var actualValue = pActual.value();
            return actualValue != null && JsonAssert.contains(expectedValue.toString(), actualValue.toString()).isEmpty();
        }
        return true;
    }

    private synchronized KafkaProducer<Object, Object> producer(final String pLogPrefix, final TopicRef pTopic) {
        return producers.computeIfAbsent(pTopic.id(), _ -> {
            final var kafkaConfig = kafkaConfigProvider.of(pTopic);
            LOG.trace("{}  Creating new producer for {}({}).", pLogPrefix, pTopic.id(), kafkaConfig.get("bootstrap.servers"));
            final var props = new Properties();
            props.putAll(kafkaConfig);
            return new KafkaProducer<>(props);
        });
    }

    public synchronized KafkaConsumer<?, ?> consumer(final String pLogPrefix, final TopicRef pTopic) {
        return consumers.computeIfAbsent(pTopic.id(), _ -> {
            final var kafkaConfig = kafkaConfigProvider.of(pTopic);
            LOG.trace("{}  Creating new consumer for {}({}).", pLogPrefix, pTopic.id(), kafkaConfig.get("bootstrap.servers"));
            final var props = new Properties();
            props.putAll(kafkaConfig);
            return new KafkaConsumer<>(props);
        });
    }

    private List<Header> kafkaHeaders(final Map<String, String> pHeaders) {
        return pHeaders
                .entrySet()
                .stream()
                .map(e -> new RecordHeader(e.getKey(), e.getValue().getBytes(StandardCharsets.UTF_8)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Object convert(final String pLogPrefix, final TopicRef pTopic, final TestRecord pRecord, final boolean pKey, final String pForcedSchema) {
        final var jsonNode = pKey ? pRecord.keyNode() : pRecord.valueNode();
        if (jsonNode == null) {
            return null;
        }
        final var expectedSerde = pKey ? pTopic.keySerde() : pTopic.valueSerde();
        final var availableSchema = registryService.lastActiveSchema(pLogPrefix, pTopic, pKey, pForcedSchema);
        if (expectedSerde == Serde.AVRO && availableSchema == null) {
            throw new KTestException("Expected Avro schema not found for " + pTopic.topic() + (pKey ? "-key@" : "-value@") + pTopic.broker(), null);
        }
        if (availableSchema == null || expectedSerde == Serde.STRING) {
            return jsonNode instanceof final TextNode textNode ? textNode.asText() : jsonNode.toString();
        }
        return jsonAvroConverter.toAvro(jsonNode, availableSchema);
    }
}
