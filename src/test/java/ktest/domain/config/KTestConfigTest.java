package ktest.domain.config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class KTestConfigTest {
    @Inject
    KTestConfig kConfig;

    @Test
    void testContent() {
        final var environments = kConfig.environments();
        assertEquals(2, environments.size());
        final var env = environments.getFirst();
        assertEquals("pi", env.name());
        assertEquals(4, env.onStartScript().size());

        final var brokers = kConfig.brokers();
        assertEquals(2, brokers.size());
        final var brk = kConfig.broker("pi_broker");
        assertEquals("pi_broker", brk.name());
        assertEquals("${concat(\"192.168.0.105\", \":\", \"9092\")}", brk.bootstrapServers());
        assertEquals("pi_registry", brk.registry());
        assertNull(brk.saslJaasConfig());
        assertNull(brk.saslMechanism());
        assertNull(brk.securityProtocol());
        assertEquals("ktest-consumer-", brk.clientIdPrefix());
        assertEquals("ktest-group", brk.groupId());

        final var registries = kConfig.registries();
        assertEquals(1, registries.size());
        final var reg = kConfig.registry("pi_registry");
        assertEquals("pi_registry", reg.name());
        assertEquals("http://192.168.0.105:8081", reg.url());
        assertNull(reg.user());
        assertNull(reg.password());
    }
}
