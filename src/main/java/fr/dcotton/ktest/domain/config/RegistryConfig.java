package fr.dcotton.ktest.domain.config;

import fr.dcotton.ktest.domain.Named;

public record RegistryConfig(String name, String url, String user, String password) implements Named {
}
