package fr.dcotton.ktest;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RegistryClientProdviderTest {
    @Inject
    private SchemaRegistryClient registryClient;

    @Test
    void testInjection() {
        System.out.println(registryClient);
    }
}
