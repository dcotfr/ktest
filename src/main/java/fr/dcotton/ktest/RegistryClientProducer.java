package fr.dcotton.ktest;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
final class RegistryClientProducer {
    @Produces
    SchemaRegistryClient produce() {
        return new CachedSchemaRegistryClient("http://localhost:8081", 128);
    }
}
