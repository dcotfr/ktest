package ktest.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.smallrye.common.constraint.NotNull;
import ktest.core.Strings;
import ktest.domain.Named;

public record BrokerConfig(@NotNull String name,
                           String description,
                           @JsonProperty(value = "bootstrap.servers", required = true) String bootstrapServers,
                           @JsonProperty("sasl.jaas.config") String saslJaasConfig,
                           @JsonProperty("sasl.mechanism") String saslMechanism,
                           @JsonProperty("security.protocol") String securityProtocol,
                           @JsonProperty("client.id.prefix") String clientIdPrefix,
                           @JsonProperty("group.id") String groupId,
                           @JsonProperty("send.timeout.sec") Long sendTimeoutSec,
                           @JsonProperty("poll.duration.ms") Long pollDurationMs,
                           String registry) implements Named {
    public String clientIdPrefix() {
        return Strings.isNullOrEmpty(clientIdPrefix) ? "ktest-consumer-" : clientIdPrefix;
    }

    public String groupId() {
        return Strings.isNullOrEmpty(groupId) ? "ktest-group" : groupId;
    }

    public long defaultSendTimeoutSec() {
        return sendTimeoutSec != null ? sendTimeoutSec : 30L;
    }

    public long defaultPollDurationMs() {
        return pollDurationMs != null ? pollDurationMs : 5000L;
    }
}
