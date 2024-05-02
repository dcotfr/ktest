package fr.dcotton.ktest.kafka;

import com.google.common.base.Strings;
import fr.dcotton.ktest.domain.config.KTestConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class KafkaConfigProvider {
    private final Map<String, Map<String, String>> kafkaConfigs = new HashMap<>();
    private final KTestConfig kConfig;

    @Inject
    KafkaConfigProvider(final KTestConfig pConfig) {
        kConfig = pConfig;
    }

    Map<String, String> of(final TopicRef pTopic) {
        return kafkaConfigs.computeIfAbsent(pTopic.id(), k -> {
            final var res = new HashMap<String, String>();

            final var brokerConfig = kConfig.broker(pTopic.broker());
            res.put("bootstrap.servers", brokerConfig.bootstrapServers());
            res.put("key.serializer", pTopic.keySerde().serializer);
            res.put("value.serializer", pTopic.valueSerde().serializer);
            res.put("key.deserializer", pTopic.keySerde().deserializer);
            res.put("value.deserializer", pTopic.valueSerde().deserializer);
            res.put("client.id", brokerConfig.clientIdPrefix() + UUID.randomUUID());
            res.put("group.id", brokerConfig.groupId());
            res.put("auto.offset.reset", "earliest");
            if (!Strings.isNullOrEmpty(brokerConfig.saslJaasConfig())) {
                res.put("sasl.jaas.config", brokerConfig.saslJaasConfig());
            }
            if (!Strings.isNullOrEmpty(brokerConfig.saslMechanism())) {
                res.put("sasl.mechanism", brokerConfig.saslMechanism());
            }
            if (!Strings.isNullOrEmpty(brokerConfig.securityProtocol())) {
                res.put("security.protocol", brokerConfig.securityProtocol());
            }
            final var registryConfig = brokerConfig.registry() != null ? kConfig.registry(brokerConfig.registry()) : null;
            if (registryConfig != null) {
                if (!Strings.isNullOrEmpty(registryConfig.url())) {
                    res.put("schema.registry.url", registryConfig.url());
                }
                if (!Strings.isNullOrEmpty(registryConfig.user())) {
                    res.put("schema.registry.basic.auth.credentials.source", "USER_INFO");
                    res.put("schema.registry.basic.auth.user.info", registryConfig.user() + ':' + registryConfig.password());
                }
            }

            return res;
        });
    }
}
