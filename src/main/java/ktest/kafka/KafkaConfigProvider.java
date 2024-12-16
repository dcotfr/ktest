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

    void reset() {
        kafkaConfigs.clear();
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
            evalAndPutIfPresent(engine, res, "sasl.jaas.config", brokerConfig.saslJaasConfig());
            evalAndPutIfPresent(engine, res, "sasl.mechanism", brokerConfig.saslMechanism());
            evalAndPutIfPresent(engine, res, "security.protocol", brokerConfig.securityProtocol());
            final var registryConfig = brokerConfig.registry() != null ? kConfig.registry(engine.evalInLine(brokerConfig.registry())) : null;
            if (registryConfig != null) {
                evalAndPutIfPresent(engine, res, "schema.registry.url", registryConfig.url());
                if (!Strings.isNullOrEmpty(registryConfig.user())) {
                    res.put("schema.registry.basic.auth.credentials.source", "USER_INFO");
                    res.put("schema.registry.basic.auth.user.info", engine.evalInLine(registryConfig.user()) + ':' + engine.evalInLine(registryConfig.password()));
                }
            }

            return res;
        });
    }

    private static void evalAndPutIfPresent(final Engine pEngine, final HashMap<String, String> pMap, final String pKey, final String pConfig) {
        if (!Strings.isNullOrEmpty(pConfig)) {
            pMap.put(pKey, pEngine.evalInLine(pConfig));
        }
    }
}
