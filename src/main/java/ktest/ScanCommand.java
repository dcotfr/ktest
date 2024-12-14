package ktest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.google.common.base.Strings;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.domain.TestRecord;
import ktest.kafka.ClusterClient;
import ktest.kafka.Serde;
import picocli.CommandLine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import static ktest.MainCommand.VERSION;

@CommandLine.Command(name = "scan", description = "Scan topic(s) to extract a sample test case.",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class ScanCommand implements Runnable {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @CommandLine.Option(names = {"-e", "--env"}, description = "Name of the environment to use.", required = true)
    private String env;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Path of the config file.", defaultValue = "ktconfig.yml")
    private String config;

    @CommandLine.Option(names = {"-i", "--inputs"}, description = "List of 'topic@broker,...' to scan.", required = true)
    private String inputs;

    private final ClusterClient kafkaClient;

    @Inject
    ScanCommand(final ClusterClient pKafkaClient) {
        kafkaClient = pKafkaClient;
    }

    @Override
    public void run() {
        final var splitInputs = Arrays.stream(inputs.split(",")).map(tr -> tr.split("@")).toList();
        final var matchAllRecord = new TestRecord(null, null, null, null);
        var stepId = 0;
        System.out.println("---");
        System.out.println("name: Sample Test Case");
        System.out.println("steps:");
        for (var input : splitInputs) {
            if (input.length != 2) {
                throw new KTestException("Malformated inputs: " + inputs, null);
            }
            final var topicRef = kafkaClient.scanSerdes(input[1], input[0]);
            final var found = kafkaClient.find(topicRef, matchAllRecord, 1);
            if (found != null) {
                System.out.println(tab(1) + "- name: Step n°" + ++stepId);
                System.out.println(tab(2) + "broker: " + topicRef.broker());
                System.out.println(tab(2) + "topic: " + topicRef.topic());
                System.out.println(tab(2) + "keySerde: " + topicRef.keySerde());
                System.out.println(tab(2) + "valueSerde: " + topicRef.valueSerde());
                System.out.println(tab(2) + "action: TODO");
                System.out.println(tab(2) + "record:");
                if (found.headers() != null && !found.headers().isEmpty()) {
                    System.out.println(tab(3) + "headers:");
                    for (final var e : found.headers().entrySet()) {
                        System.out.println(tab(4) + e.getKey() + ": " + e.getValue());
                    }
                }
                System.out.println(tab(3) + "key: " + formattedContent(topicRef.keySerde(), found.key()));
                System.out.println(tab(3) + "value: " + formattedContent(topicRef.valueSerde(), found.value()));
            }
        }
    }

    private static String tab(final int pCount) {
        return Strings.repeat("  ", pCount);
    }

    private static String formattedContent(final Serde pSerde, final Object pContent) {
        if (pSerde == Serde.STRING) {
            return pContent.toString();
        }

        final var writer = new StringWriter();
        writer.write("|\n");
        try {
            MAPPER.writeValue(writer, MAPPER.readTree(pContent.toString()));
        } catch (final IOException e) {
            throw new KTestException("Failed to parse scanned value.", e);
        }
        final var buffer = writer.getBuffer();
        final var bufferEnd = buffer.length() - 1;
        if (buffer.charAt(bufferEnd) == '\n') {
            buffer.deleteCharAt(bufferEnd);
        }
        return writer.toString().replace("\n", "\n" + tab(4));
    }
}
