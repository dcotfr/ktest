package ktest.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.schemaregistry.SchemaProvider;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaProvider;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.json.JsonSchemaProvider;
import io.confluent.kafka.schemaregistry.protobuf.ProtobufSchema;
import io.confluent.kafka.schemaregistry.protobuf.ProtobufSchemaProvider;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.core.Strings;
import ktest.domain.config.KTestConfig;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RegistryService {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryService.class);

    private final Map<String, SchemaRegistryClient> registries = new HashMap<>();
    private final Map<String, SchemaWrapper> schemas = new HashMap<>();
    private final KTestConfig kConfig;
    private final KafkaConfigProvider kafkaConfigProvider;
    private final ObjectMapper objectMapper;

    @Inject
    RegistryService(final KTestConfig pConfig, final KafkaConfigProvider pKafkaConfigProvider, final ObjectMapper pObjectMapper) {
        kConfig = pConfig;
        kafkaConfigProvider = pKafkaConfigProvider;
        objectMapper = pObjectMapper;
    }

    @PreDestroy
    public void destroy() {
        for (final var client : registries.values()) {
            try {
                client.close();
            } catch (final IOException e) {
                LOG.trace("  Registry client {} final closing failed with {}.", client, e.getMessage());
            }
        }
    }

    @Retry
    public SchemaWrapper lastActiveSchema(final String pLogPrefix, final TopicRef pTopic, final boolean pKey, final String pForcedSchema) {
        var schemaName = pForcedSchema;
        if (Strings.isNullOrEmpty(pForcedSchema)) {
            schemaName = pTopic.topic() + (pKey ? "-key" : "-value");
        }
        final var schemaKey = schemaName + "@" + pTopic.broker();
        final var registryClient = registryClient(pLogPrefix, pTopic);
        if (registryClient == null) {
            return null;
        }
        synchronized (this) {
            if (schemas.containsKey(schemaKey)) {
                return schemas.get(schemaKey);
            }

            LOG.trace("{}  Trying to get last active schema of {}.", pLogPrefix, schemaKey);
            SchemaWrapper res = null;
            try {
                final var rawSchemas = registryClient.getSchemas(schemaName, false, true);
                final var rawSchema = (rawSchemas == null || rawSchemas.isEmpty()) ? null : rawSchemas.getFirst();
                if (rawSchema != null) {
                    res = switch (rawSchema.schemaType()) {
                        case "AVRO" -> SchemaWrapper.ofAvro(new Schema.Parser().parse(rawSchema.canonicalString()));
                        case "JSON" -> SchemaWrapper.ofJson(objectMapper.readTree(rawSchema.canonicalString()));
                        case "PROTOBUF" ->
                                SchemaWrapper.ofProtobuf(new ProtobufSchema(rawSchema.canonicalString()).toDescriptor());
                        default -> null;
                    };
                }
            } catch (final IOException | RestClientException | IllegalArgumentException | IllegalStateException |
                           AvroTypeException e) {
                throw new KTestException("Error while getting schema of " + schemaKey, e);
            }
            schemas.put(schemaKey, res);
            return res;
        }
    }

    private synchronized SchemaRegistryClient registryClient(final String pLogPrefix, final TopicRef pTopic) {
        final var registryRef = kConfig.broker(pTopic.broker()).registry();
        final var registryConfig = registryRef != null ? kConfig.registry(registryRef) : null;
        if (registryConfig == null) {
            return null;
        }

        return registries.computeIfAbsent(registryRef, _ -> {
            LOG.trace("{}  Connecting to registry {}({}).", pLogPrefix, registryRef, registryConfig.url());
            final var providers = List.<SchemaProvider>of(new AvroSchemaProvider(), new JsonSchemaProvider(), new ProtobufSchemaProvider());
            return new CachedSchemaRegistryClient(registryConfig.url(), 256, providers, kafkaConfigProvider.of(pTopic));
        });
    }
}
