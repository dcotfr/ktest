package fr.dcotton.ktest.kafka;

import fr.dcotton.ktest.core.KTestException;
import fr.dcotton.ktest.domain.TestRecord;
import fr.dcotton.ktest.domain.config.KTestConfig;
import fr.dcotton.ktest.kafka.avrogen.JsonAvroConverter;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static fr.dcotton.ktest.core.AnsiColor.BLUE;

@RegisterForReflection(registerFullHierarchy = true)
@ApplicationScoped
public class ClusterClient {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterClient.class);

    private final Map<String, KafkaProducer> producers = new HashMap<>();
    private final KTestConfig kConfig;
    private final KafkaConfigProvider kafkaConfigProvider;
    private final RegistryService registryService;
    private final JsonAvroConverter jsonAvroConverter;

    @Inject
    public ClusterClient(final KTestConfig pConfig, final KafkaConfigProvider pKafkaConfigProvider,
                         final RegistryService pRegistryService, final JsonAvroConverter pJsonAvroConverter) {
        kConfig = pConfig;
        kafkaConfigProvider = pKafkaConfigProvider;
        registryService = pRegistryService;
        jsonAvroConverter = pJsonAvroConverter;
    }

    public void send(final TopicRef pTopic, final TestRecord pRecord) {
        final var producer = producer(pTopic);
        LOG.trace("{}      Sending record to {}.", BLUE, pTopic.id());

        final var rec = new ProducerRecord<>(pTopic.topic(),
                null,
                pRecord.timestamp(),
                convert(pTopic, pRecord, true),
                convert(pTopic, pRecord, false),
                kafkaHeaders(pRecord.headers()));
        try {
            producer.send(rec).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KTestException("Failed to send record to " + pTopic.id(), e);
        }
    }

    private KafkaProducer producer(final TopicRef pTopic) {
        return producers.computeIfAbsent(pTopic.id(), k -> {
            final var kafkaConfig = kafkaConfigProvider.of(pTopic);
            LOG.trace("{}      Creating new producer for {}({}).", BLUE, pTopic.id(), kafkaConfig.get("bootstrap.servers"));
            final var props = new Properties();
            props.putAll(kafkaConfig);
            return new KafkaProducer<>(props);
        });
    }

    private List<Header> kafkaHeaders(final Map<String, String> pHeaders) {
        return pHeaders
                .entrySet()
                .stream()
                .map(e -> new RecordHeader(e.getKey(), e.getValue().getBytes(StandardCharsets.UTF_8)))
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    private Object convert(final TopicRef pTopic, final TestRecord pRecord, boolean pKey) {
        final var jsonNode = pKey ? pRecord.keyNode() : pRecord.valueNode();
        if (jsonNode == null) {
            return null;
        }
        final var expectedSerde = pKey ? pTopic.keySerde() : pTopic.valueSerde();
        final var availableSchema = registryService.lastActiveSchema(pTopic, pKey);
        if (expectedSerde == Serde.AVRO && availableSchema == null) {
            throw new KTestException("Expected Avro schema not found for " + pTopic.id() + (pKey ? "key" : "value"), null);
        }
        return (availableSchema == null || expectedSerde == Serde.STRING) ?
                jsonNode.toString() : jsonAvroConverter.toAvro(jsonNode, availableSchema);
    }
}
