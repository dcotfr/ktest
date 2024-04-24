package fr.dcotton.ktest.kafka;

import fr.dcotton.ktest.core.KTestException;
import fr.dcotton.ktest.domain.config.KTestConfig;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static fr.dcotton.ktest.core.AnsiColor.BLUE;

@ApplicationScoped
class RegistryService {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryService.class);

    private final Map<String, SchemaRegistryClient> registries = new HashMap<>();
    private final Map<String, Schema> schemas = new HashMap<>();
    private final KTestConfig kConfig;
    private final KafkaConfigProvider kafkaConfigProvider;

    @Inject
    RegistryService(final KTestConfig pConfig, final KafkaConfigProvider pKafkaConfigProvider) {
        kConfig = pConfig;
        kafkaConfigProvider = pKafkaConfigProvider;
    }

    Schema lastActiveSchema(final TopicRef pTopic, final boolean pKey) {
        final var schemaSuffix = pKey ? "-key" : "-value";
        final var schemaKey = pTopic.id() + schemaSuffix;
        if (schemas.containsKey(schemaKey)) {
            return schemas.get(schemaKey);
        }

        Schema res = null;
        final var registryClient = registryClient(pTopic);
        if (registryClient != null) {
            LOG.trace("{}      Trying to get last active schema of {}.", BLUE, pTopic.id() + schemaSuffix);
            try {
                final var rawSchemas = registryClient.getSchemas(pTopic.topic() + schemaSuffix, false, true);
                final var rawSchema = (rawSchemas != null && !rawSchemas.isEmpty()) ? rawSchemas.getFirst() : null;
                res = rawSchema != null ? new Schema.Parser().parse(rawSchema.canonicalString()) : null;
            } catch (final IOException | RestClientException e) {
                throw new KTestException("Error while getting schema of " + schemaKey, e);
            }
        }
        schemas.put(schemaKey, res);
        return res;
    }

    private SchemaRegistryClient registryClient(final TopicRef pTopic) {
        final var registryRef = kConfig.broker(pTopic.broker()).registry();
        final var registryConfig = registryRef != null ? kConfig.registry(registryRef) : null;
        if (registryConfig == null) {
            return null;
        }

        return registries.computeIfAbsent(registryRef, k -> {
            LOG.trace("{}      Connecting to registry {}({}).", BLUE, registryRef, registryConfig.url());
            return new CachedSchemaRegistryClient(registryConfig.url(), 128, kafkaConfigProvider.of(pTopic));
        });
    }
}
