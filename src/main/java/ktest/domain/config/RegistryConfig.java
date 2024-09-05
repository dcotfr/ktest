package ktest.domain.config;

import ktest.domain.Named;

public record RegistryConfig(String name, String url, String user, String password) implements Named {
}
