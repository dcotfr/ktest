package fr.dcotton.ktest.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.dcotton.ktest.core.KTestException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record TestCase(String name, String beforeAll, List<Step> steps, String afterAll) implements Named {
    public List<String> beforeAllScript() {
        return beforeAll != null ? Arrays.stream(beforeAll.split("\n")).toList() : Collections.emptyList();
    }

    public List<String> afterAllScript() {
        return afterAll != null ? Arrays.stream(afterAll.split("\n")).toList() : Collections.emptyList();
    }

    public static TestCase load(final String pFile) {
        try {
            return new ObjectMapper(new YAMLFactory()).readValue(readFile(pFile), TestCase.class);
        } catch (final JsonProcessingException e) {
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
