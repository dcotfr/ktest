package ktest.kafka;

import com.fasterxml.jackson.databind.node.TextNode;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.domain.TestRecord;
import ktest.json.JsonAssert;
import ktest.kafka.avrogen.JsonAvroConverter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static ktest.core.AnsiColor.BLUE;

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

    public void send(final TopicRef pTopic, final TestRecord pRecord) {
        final var producer = producer(pTopic);
        LOG.trace("{}      Sending record to {}.", BLUE, pTopic.id());

        final var rec = new ProducerRecord<>(pTopic.topic(),
                null,
                pRecord.longTimestamp(),
                convert(pTopic, pRecord, true),
                convert(pTopic, pRecord, false),
                kafkaHeaders(pRecord.headers()));
        try {
            final var futur = producer.send(rec);
            producer.flush();
            futur.get(30, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            throw new KTestException("Failed to send record to " + pTopic.id(), e);
        } catch (final InterruptedException e) {
            LOG.error("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    public FoundRecord find(final TopicRef pTopic, final TestRecord pRecord, final int pBackOffset) {
        final var consumer = consumer(pTopic);
        var lastOffsetReached = false;
        final var lastOffset = resetConsumer(consumer, pTopic.topic(), pBackOffset);
        if (lastOffset >= 0) {
            while (true) {
                final var recs = consumer.poll(Duration.ofMillis(1000));
                if (lastOffsetReached && recs.isEmpty()) {
                    break;
                }
                for (final var o : recs) {
                    if (o instanceof ConsumerRecord<?, ?> rec) {
                        if (assertRecord(pRecord, rec)) {
                            return new FoundRecord(rec);
                        }
                        if (rec.offset() >= lastOffset) {
                            lastOffsetReached = true;
                        }
                    }
                }
            }
        }
        return null;
    }

    private long resetConsumer(final KafkaConsumer<?, ?> pConsumer, final String pTopicName, final int pBackOffset) {
        final var partitions = pConsumer.partitionsFor(pTopicName).stream()
                .map(p -> new TopicPartition(pTopicName, p.partition())).toList();
        var lastOffset = 0L;
        if (!partitions.isEmpty()) {
            pConsumer.assign(partitions);
            for (final var e : pConsumer.endOffsets(partitions).entrySet()) {
                lastOffset = Math.max(lastOffset, e.getValue());
                pConsumer.seek(e.getKey(), Math.max(e.getValue() - pBackOffset, 0));
            }
        }
        return lastOffset - 1;
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

    private synchronized KafkaProducer<Object, Object> producer(final TopicRef pTopic) {
        return producers.computeIfAbsent(pTopic.id(), _ -> {
            final var kafkaConfig = kafkaConfigProvider.of(pTopic);
            LOG.trace("{}      Creating new producer for {}({}).", BLUE, pTopic.id(), kafkaConfig.get("bootstrap.servers"));
            final var props = new Properties();
            props.putAll(kafkaConfig);
            return new KafkaProducer<>(props);
        });
    }

    private synchronized KafkaConsumer<?, ?> consumer(final TopicRef pTopic) {
        return consumers.computeIfAbsent(pTopic.id() + "-" + Thread.currentThread().threadId(), _ -> {
            final var kafkaConfig = kafkaConfigProvider.of(pTopic);
            LOG.trace("{}      Creating new consumer for {}({}).", BLUE, pTopic.id(), kafkaConfig.get("bootstrap.servers"));
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

    private Object convert(final TopicRef pTopic, final TestRecord pRecord, final boolean pKey) {
        final var jsonNode = pKey ? pRecord.keyNode() : pRecord.valueNode();
        if (jsonNode == null) {
            return null;
        }
        final var expectedSerde = pKey ? pTopic.keySerde() : pTopic.valueSerde();
        final var availableSchema = registryService.lastActiveSchema(pTopic, pKey);
        if (expectedSerde == Serde.AVRO && availableSchema == null) {
            throw new KTestException("Expected Avro schema not found for " + pTopic.id() + (pKey ? "key" : "value"), null);
        }
        if (availableSchema == null || expectedSerde == Serde.STRING) {
            return jsonNode instanceof TextNode textNode ? textNode.asText() : jsonNode.toString();
        }
        return jsonAvroConverter.toAvro(jsonNode, availableSchema);
    }
}
