package ktest.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import ktest.domain.Named;

public record BrokerConfig(String name, @JsonProperty("bootstrap.servers") String bootstrapServers,
                           @JsonProperty("sasl.jaas.config") String saslJaasConfig,
                           @JsonProperty("sasl.mechanism") String saslMechanism,
                           @JsonProperty("security.protocol") String securityProtocol,
                           @JsonProperty("client.id.prefix") String clientIdPrefix,
                           @JsonProperty("group.id") String groupId,
                           String registry) implements Named {
    public String clientIdPrefix() {
        return Strings.isNullOrEmpty(clientIdPrefix) ? "ktest-consumer-" : clientIdPrefix;
    }

    public String groupId() {
        return Strings.isNullOrEmpty(groupId) ? "ktest-group" : groupId;
    }
}
