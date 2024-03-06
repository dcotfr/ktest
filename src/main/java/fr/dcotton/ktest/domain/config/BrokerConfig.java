package fr.dcotton.ktest.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.dcotton.ktest.domain.Named;

public record BrokerConfig(String name, @JsonProperty("bootstrap.servers") String bootstrapServers,
                           @JsonProperty("sasl.jaas.config") String saslJaasConfig,
                           @JsonProperty("sasl.mechanism") String saslMechanism,
                           @JsonProperty("security.protocol") String securityProtocol,
                           String registry) implements Named {
}
