package ktest.scan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import ktest.core.KTestException;
import ktest.kafka.ScanResult;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static ktest.core.Strings.repeat;

@ApplicationScoped
public class YamlScanRenderer implements ScanRenderer {
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(
            new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public String render(final ScanResult pResult) {
        final var res = new ArrayList<String>();
        res.add("---");
        res.add("name: " + pResult.name());
        res.add("steps:");

        for (final var step : pResult.steps()) {
            res.addAll(renderStep(step));
        }

        return String.join("\n", res);
    }

    private List<String> renderStep(final ScanResult.Step pStep) {
        final var res = new ArrayList<String>();
        res.add(tab(1) + "- name: Step n°" + pStep.id());
        res.add(tab(2) + "broker: " + pStep.broker());
        res.add(tab(2) + "topic: " + pStep.topic());
        res.add(tab(2) + "keySerde: " + pStep.keySerde());
        res.add(tab(2) + "valueSerde: " + pStep.valueSerde());
        res.add(tab(2) + "action: " + pStep.action());
        res.add(tab(2) + "record:");
        renderRecord(res, pStep.record());
        return res;
    }

    private void renderRecord(final List<String> pResult, final ScanResult.RecordData pRecord) {
        if (pRecord.headers() != null && !pRecord.headers().isEmpty()) {
            pResult.add(tab(3) + "headers:");
            for (final var e : pRecord.headers().entrySet()) {
                pResult.add(tab(4) + e.getKey() + ": " + e.getValue());
            }
        }
        pResult.add(tab(3) + "key: " + formattedContent(pRecord.key()));
        pResult.add(tab(3) + "value: " + formattedContent(pRecord.value()));
    }

    private static String formattedContent(final Object pContent) {
        if (pContent == null) {
            return "null";
        }

        final var str = pContent.toString();
        if (str.startsWith("{") || str.startsWith("[")) {
            final var writer = new StringWriter();
            writer.write("|\n");
            try {
                YAML_MAPPER.writeValue(writer, YAML_MAPPER.readTree(str));
            } catch (final Exception e) {
                throw new KTestException("Failed to parse scanned value.", e);
            }
            return writer.toString().stripLeading().replace("\n", "\n" + tab(4));
        }

        return str;
    }

    private static String tab(final int pCount) {
        return repeat("  ", pCount);
    }
}
