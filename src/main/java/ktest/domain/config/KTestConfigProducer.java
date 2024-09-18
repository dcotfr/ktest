package ktest.domain.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
final class KTestConfigProducer {
    private KTestConfig kConfig;

    @Inject
    private CommandLine.ParseResult parsedCommand;

    @Produces
    @DefaultBean
    synchronized KTestConfig loadedConfig() {
        if (kConfig == null) {
            final var subCommand = parsedCommand.subcommand();
            final var cfgFile = subCommand != null ? subCommand.matchedOption("c") : null;
            final var cfgPath = cfgFile != null ? cfgFile.getValue().toString() : "ktconfig.yml";
            try {
                kConfig = new ObjectMapper(new YAMLFactory()).readValue(readFile(cfgPath), KTestConfig.class);
                final var envOpt = subCommand != null ? subCommand.matchedOption("e") : null;
                kConfig.currentEnvironment(envOpt != null ? envOpt.getValue() : "");
            } catch (final JsonProcessingException e) {
                throw new KTestException("Invalid syntax in config file " + cfgPath, e);
            }
        }
        return kConfig;
    }

    private String readFile(final String pFile) {
        Path absolutePath = null;
        try {
            absolutePath = Path.of(pFile).toAbsolutePath();
            return Files.readString(absolutePath);
        } catch (final Throwable e) {
            throw new KTestException("Failed to read config file " + (absolutePath != null ? absolutePath : pFile), e);
        }
    }
}
