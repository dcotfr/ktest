package ktest.kafka;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.domain.config.KTestConfig;
import ktest.script.Engine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class KafkaConfigProvider {
    private final Map<String, Map<String, String>> kafkaConfigs = new HashMap<>();
    private final KTestConfig kConfig;
    private final Instance<Engine> engineFactory;

    @Inject
    KafkaConfigProvider(final KTestConfig pConfig, final Instance<Engine> pEngineFactory) {
        kConfig = pConfig;
        engineFactory = pEngineFactory;
    }

    Map<String, String> of(final TopicRef pTopic) {
        return kafkaConfigs.computeIfAbsent(pTopic.id(), k -> {
            final var res = new HashMap<String, String>();

            final var engine = engineFactory.get();
            final var brokerConfig = kConfig.broker(engine.evalInLine(pTopic.broker()));
            res.put("bootstrap.servers", engine.evalInLine(brokerConfig.bootstrapServers()));
            res.put("key.serializer", engine.evalInLine(pTopic.keySerde().serializer));
            res.put("value.serializer", engine.evalInLine(pTopic.valueSerde().serializer));
            res.put("key.deserializer", engine.evalInLine(pTopic.keySerde().deserializer));
            res.put("value.deserializer", engine.evalInLine(pTopic.valueSerde().deserializer));
            res.put("client.id", engine.evalInLine(brokerConfig.clientIdPrefix()) + UUID.randomUUID());
            res.put("group.id", engine.evalInLine(brokerConfig.groupId()));
            res.put("auto.offset.reset", "earliest");
            res.put("acks", "all");
            if (!Strings.isNullOrEmpty(brokerConfig.saslJaasConfig())) {
                res.put("sasl.jaas.config", engine.evalInLine(brokerConfig.saslJaasConfig()));
            }
            if (!Strings.isNullOrEmpty(brokerConfig.saslMechanism())) {
                res.put("sasl.mechanism", engine.evalInLine(brokerConfig.saslMechanism()));
            }
            if (!Strings.isNullOrEmpty(brokerConfig.securityProtocol())) {
                res.put("security.protocol", engine.evalInLine(brokerConfig.securityProtocol()));
            }
            final var registryConfig = brokerConfig.registry() != null ? kConfig.registry(engine.evalInLine(brokerConfig.registry())) : null;
            if (registryConfig != null) {
                if (!Strings.isNullOrEmpty(registryConfig.url())) {
                    res.put("schema.registry.url", engine.evalInLine(registryConfig.url()));
                }
                if (!Strings.isNullOrEmpty(registryConfig.user())) {
                    res.put("schema.registry.basic.auth.credentials.source", "USER_INFO");
                    res.put("schema.registry.basic.auth.user.info", engine.evalInLine(registryConfig.user()) + ':' + engine.evalInLine(registryConfig.password()));
                }
            }

            return res;
        });
    }
}
