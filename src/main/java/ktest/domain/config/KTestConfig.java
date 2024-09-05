package ktest.domain.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import ktest.domain.Named;

import java.util.Collections;
import java.util.List;

public class KTestConfig {
    @JsonProperty
    private List<RegistryConfig> registries;
    @JsonProperty
    private List<BrokerConfig> brokers;
    @JsonProperty
    private List<EnvironmentConfig> environments;

    private String currentEnvironment;

    public List<RegistryConfig> registries() {
        return registries != null ? registries : Collections.emptyList();
    }

    public RegistryConfig registry(final String pName) {
        return find(registries(), pName);
    }

    public List<BrokerConfig> brokers() {
        return brokers != null ? brokers : Collections.emptyList();
    }

    public BrokerConfig broker(final String pName) {
        return find(brokers(), pName);
    }

    public List<EnvironmentConfig> environments() {
        return environments != null ? environments : Collections.emptyList();
    }

    public EnvironmentConfig currentEnvironment() {
        return find(environments(), currentEnvironment);
    }

    void currentEnvironment(final String pName) {
        currentEnvironment = pName;
    }

    private <T extends Named> T find(final List<T> pList, final String pName) {
        if (pName != null) {
            for (final var res : pList) {
                if (pName.equals(res.name())) {
                    return res;
                }
            }
        }
        return null;
    }
}
