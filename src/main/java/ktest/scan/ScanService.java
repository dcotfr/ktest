package ktest.scan;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ktest.kafka.KafkaConfigProvider;
import ktest.kafka.RegistryService;
import ktest.kafka.Serde;
import ktest.kafka.TopicRef;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

@ApplicationScoped
public class ScanService {
    private static final Logger LOG = LoggerFactory.getLogger(ScanService.class);

    private final KafkaConfigProvider kafkaConfigProvider;
    private final RegistryService registryService;

    @Inject
    public ScanService(final KafkaConfigProvider pKafkaConfigProvider, final RegistryService pRegistryService) {
        kafkaConfigProvider = pKafkaConfigProvider;
        registryService = pRegistryService;
    }

    public List<ScanParsedInput> listUserTopics(final String pLogPrefix, final String pBrokerId) {
        LOG.trace("{}  Listing topics of broker {}.", pLogPrefix, pBrokerId);
        return listUserTopicsNames(pBrokerId).stream()
                .map(topic -> new ScanParsedInput(pBrokerId, topic)).toList();
    }

    public boolean topicExists(final String pBrokerId, final String pTopicName) {
        LOG.trace("Checking if topic {} exists on {}.", pTopicName, pBrokerId);
        return listUserTopicsNames(pBrokerId).contains(pTopicName);
    }

    public TopicSchemas lastActiveSchemas(final String pLogPrefix, final String pBroker, final String pTopic) {
        final var temporaryTopicRef = new TopicRef(pBroker, pTopic, Serde.BYTES, Serde.BYTES);
        final var keySchema = registryService.lastActiveSchema(pLogPrefix, temporaryTopicRef, true, null);
        final var valueSchema = registryService.lastActiveSchema(pLogPrefix, temporaryTopicRef, false, null);
        kafkaConfigProvider.reset();
        return new TopicSchemas(keySchema, valueSchema);
    }

    public TopicRef scanSerdes(final String pLogPrefix, final String pBroker, final String pTopic) {
        final var topicSchemas = lastActiveSchemas(pLogPrefix, pBroker, pTopic);
        final var keySerde = topicSchemas.keySchema() != null ? topicSchemas.keySchema().serde() : Serde.STRING;
        final var valueSerde = topicSchemas.valueSchema() != null ? topicSchemas.valueSchema().serde() : Serde.STRING;
        return new TopicRef(pBroker, pTopic, keySerde, valueSerde);
    }

    private List<String> listUserTopicsNames(final String pBrokerId) {
        final var kafkaConfig = kafkaConfigProvider.of(new TopicRef(pBrokerId, "", Serde.BYTES, Serde.BYTES));
        final var props = new Properties();
        props.putAll(kafkaConfig);
        try (final var consumer = new KafkaConsumer<>(props)) {
            return consumer.listTopics().keySet().stream()
                    .filter(topic -> !topic.startsWith("_")).sorted()
                    .toList();
        }
    }
}
