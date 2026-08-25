package ktest;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.core.LogTab;
import ktest.domain.TestRecord;
import ktest.domain.config.KTestConfig;
import ktest.kafka.ClusterClient;
import ktest.kafka.FoundRecord;
import ktest.kafka.ScanResult;
import ktest.kafka.TopicRef;
import ktest.scan.ScanParsedInput;
import ktest.scan.ScanService;
import ktest.scan.YamlScanRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ktest.MainCommand.VERSION;
import static ktest.core.AnsiColor.BLUE;

@CommandLine.Command(name = "scan", description = "Scan topic(s) to extract a sample test case.",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class ScanCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ScanCommand.class);

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
    private final ScanService scanService;
    private final YamlScanRenderer yamlRenderer;

    @Inject
    ScanCommand(final Instance<KTestConfig> pConfigFactory, final ClusterClient pKafkaClient, final ScanService pScanService,
                final YamlScanRenderer pRenderer) {
        configFactory = pConfigFactory;
        kafkaClient = pKafkaClient;
        scanService = pScanService;
        yamlRenderer = pRenderer;
    }

    @Override
    public void run() {
        final var parsedInputs = parsedInputs(inputs);
        final var matchAllRecord = new TestRecord(null, null, null, null);
        final var scanResult = new ScanResult("Sample Test Case", new ArrayList<>());
        var stepId = 0;

        final var logPrefix = LogTab.tab(BLUE, 1, false);
        for (final var input : parsedInputs) {
            LOG.info("Scanning last record of {}@{}", input.topic(), input.broker());
            final var topicRef = scanService.scanSerdes(logPrefix, input.broker(), input.topic());
            final var found = kafkaClient.find(logPrefix, topicRef, matchAllRecord, 1);
            if (found != null) {
                scanResult.addStep(toStep(found, topicRef, ++stepId));
            } else {
                LOG.warn("No record found in {}@{}", input.topic(), input.broker());
            }
        }

        final var rendered = yamlRenderer.render(scanResult);
        try {
            Files.write(Path.of(output), rendered.getBytes(StandardCharsets.UTF_8));
            LOG.info("Sample test case created in {} file.", output);
        } catch (final Exception e) {
            throw new KTestException("Failed to write sample file.", e);
        }
    }

    private ScanResult.Step toStep(final FoundRecord pRecord, final TopicRef pTopicRef, final int pStepId) {
        return new ScanResult.Step(
                pStepId,
                pTopicRef.broker(),
                pTopicRef.topic(),
                pTopicRef.keySerde().name(),
                pTopicRef.valueSerde().name(),
                "TODO",
                new ScanResult.RecordData(
                        pRecord.headers() != null ? pRecord.headers() : new java.util.TreeMap<>(),
                        pRecord.key(),
                        pRecord.value()
                )
        );
    }

    private List<ScanParsedInput> parsedInputs(final String pInputs) {
        final var res = new ArrayList<ScanParsedInput>();
        final var cfg = configFactory.get();
        for (final var tr : pInputs.split(",")) {
            if (tr.trim().startsWith("@")) {
                final var broker = tr.substring(1);
                LOG.info("Auto scan broker '{}'...", broker);
                res.addAll(scanService.listUserTopics(LogTab.tab(BLUE, 1, false), broker));
                continue;
            }
            final var split = tr.split("@");
            final var topic = split.length >= 1 ? split[0].trim() : null;
            var broker = split.length == 2 ? split[1].trim() : null;
            broker = (broker == null && cfg.brokers().size() == 1) ? cfg.brokers().getFirst().name() : broker;
            if (topic == null || broker == null) {
                throw new KTestException("Malformed inputs: " + inputs, null);
            }
            if (cfg.broker(broker) == null) {
                throw new KTestException("Unknown broker '" + broker + "' in inputs: " + inputs, null);
            }
            res.add(new ScanParsedInput(broker, topic));
        }
        return res;
    }
}
