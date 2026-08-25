package ktest.kafka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ScanResult {
    private final String name;
    private final List<Step> steps;

    public ScanResult(final String pName, final List<Step> pSteps) {
        name = pName;
        steps = pSteps != null ? pSteps : new ArrayList<>();
    }

    public String name() {
        return name;
    }

    public List<Step> steps() {
        return Collections.unmodifiableList(steps);
    }

    public void addStep(final Step pStep) {
        steps.add(pStep);
    }

    public record Step(int id, String broker, String topic, String keySerde, String valueSerde, String action,
                       RecordData record) {
    }

    public record RecordData(Map<String, String> headers, Object key, Object value) {
    }
}
