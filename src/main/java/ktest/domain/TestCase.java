package ktest.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ktest.core.KTestException;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RegisterForReflection
public record TestCase(String name, String beforeAll, List<Step> steps, String afterAll) implements Named {
    public List<String> beforeAllScript() {
        return beforeAll != null ? Arrays.stream(beforeAll.split("\n")).toList() : Collections.emptyList();
    }

    public List<String> afterAllScript() {
        return afterAll != null ? Arrays.stream(afterAll.split("\n")).toList() : Collections.emptyList();
    }

    public static List<TestCase> load(final String pFile) {
        try {
            final var yamlParser = new YAMLFactory().createParser(readFile(pFile));
            final var typeReference = new TypeReference<TestCase>() {
            };
            return new ObjectMapper()
                    .readValues(yamlParser, typeReference)
                    .readAll();
        } catch (final IOException e) {
            throw new KTestException("Invalid test case file syntax.", e);
        }
    }

    private static String readFile(final String pFile) {
        Path absolutePath = null;
        try {
            absolutePath = Path.of(pFile).toAbsolutePath();
            return Files.readString(absolutePath);
        } catch (final Throwable e) {
            throw new KTestException("Failed to read test case file " + (absolutePath != null ? absolutePath : pFile), e);
        }
    }
}
