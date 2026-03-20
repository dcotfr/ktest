package ktest.domain.config;

import io.smallrye.common.constraint.NotNull;
import ktest.domain.Named;

public record RegistryConfig(@NotNull String name, @NotNull String url, String user, String password) implements Named {
    @Override
    public String toString() {
        return "RegistryConfig[name=" + name + ", url=" + url + ", user=*****, password=*****]";
    }
}
