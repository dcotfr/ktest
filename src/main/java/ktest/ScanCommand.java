package ktest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.google.common.base.Strings;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.domain.TestRecord;
import ktest.domain.config.KTestConfig;
import ktest.kafka.ClusterClient;
import ktest.kafka.FoundRecord;
import ktest.kafka.Serde;
import ktest.kafka.TopicRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ktest.MainCommand.VERSION;

@CommandLine.Command(name = "scan", description = "Scan topic(s) to extract a sample test case.",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class ScanCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ScanCommand.class);
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @CommandLine.Option(names = {"-e", "--env"}, description = "Name of the environment to use.", required = true)
    private String env;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Path of the config file.", defaultValue = "ktconfig.yml")
    private String config;

    @CommandLine.Option(names = {"-i", "--inputs"}, description = "List of 'topic@broker,...' (or '@broker' ref) to scan.", required = true)
    private String inputs;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Path of output sample file.", defaultValue = "ktsample.yml")
    private String output;

    private final Instance<KTestConfig> configFactory;
    private final ClusterClient kafkaClient;

    @Inject
    ScanCommand(final Instance<KTestConfig> pConfigFactory, final ClusterClient pKafkaClient) {
        configFactory = pConfigFactory;
        kafkaClient = pKafkaClient;
    }

    @Override
    public void run() {
        final var parsedInputs = parsedInputs(inputs);
        final var matchAllRecord = new TestRecord(null, null, null, null);
        final var res = new ArrayList<String>();
        var stepId = 0;

        res.add("---");
        res.add("name: Sample Test Case");
        res.add("steps:");
        for (var input : parsedInputs) {
            LOG.info("Scanning last record of {}@{}", input.topic, input.broker);
            final var topicRef = kafkaClient.scanSerdes(input.broker, input.topic);
            final var found = kafkaClient.find(topicRef, matchAllRecord, 1);
            if (found != null) {
                res.addAll(createStepFromRecord(found, topicRef, ++stepId));
            } else {
                LOG.warn("No record found in {}@{}", input.topic, input.broker);
            }
        }
        try {
            Files.write(Path.of(output), res, StandardCharsets.UTF_8);
            LOG.info("Sample test case created in {} file.", output);
        } catch (final IOException e) {
            throw new KTestException("Failed to write sample file.", e);
        }
    }

    private List<ParsedInput> parsedInputs(final String pInputs) {
        final var res = new ArrayList<ParsedInput>();
        final var cfg = configFactory.get();
        for (final var tr : pInputs.split(",")) {
            if (tr.trim().startsWith("@")) {
                res.addAll(autoScan(tr.substring(1)));
                continue;
            }
            final var split = tr.split("@");
            final var topic = split.length >= 1 ? split[0].trim() : null;
            var broker = split.length == 2 ? split[1].trim() : null;
            broker = (broker == null && cfg.brokers().size() == 1) ? cfg.brokers().getFirst().name() : broker;
            if (topic == null || broker == null) {
                throw new KTestException("Malformated inputs: " + inputs, null);
            }
            if (cfg.broker(broker) == null) {
                throw new KTestException("Unknown broker '" + broker + "' in inputs: " + inputs, null);
            }
            res.add(new ParsedInput(broker, topic));
        }
        return res;
    }

    private List<ParsedInput> autoScan(final String pBroker) {
        LOG.info("Auto scan broker '{}'...", pBroker);
        final var consumer = kafkaClient.consumer(new TopicRef(pBroker, "", Serde.BYTES, Serde.BYTES));
        return consumer.listTopics().keySet().stream()
                .filter(topic -> !topic.startsWith("_"))
                .sorted()
                .map(topic -> new ParsedInput(pBroker, topic))
                .toList();
    }

    private static List<String> createStepFromRecord(final FoundRecord pRecord, final TopicRef pTopicRef, final int pStepId) {
        final var res = new ArrayList<String>();
        res.add(tab(1) + "- name: Step n°" + pStepId);
        res.add(tab(2) + "broker: " + pTopicRef.broker());
        res.add(tab(2) + "topic: " + pTopicRef.topic());
        res.add(tab(2) + "keySerde: " + pTopicRef.keySerde());
        res.add(tab(2) + "valueSerde: " + pTopicRef.valueSerde());
        res.add(tab(2) + "action: TODO");
        res.add(tab(2) + "record:");
        if (pRecord.headers() != null && !pRecord.headers().isEmpty()) {
            res.add(tab(3) + "headers:");
            for (final var e : pRecord.headers().entrySet()) {
                res.add(tab(4) + e.getKey() + ": " + e.getValue());
            }
        }
        res.add(tab(3) + "key: " + formattedContent(pTopicRef.keySerde(), pRecord.key()));
        res.add(tab(3) + "value: " + formattedContent(pTopicRef.valueSerde(), pRecord.value()));

        return res;
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

    private record ParsedInput(String broker, String topic) {
    }
}
