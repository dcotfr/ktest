package ktest.domain.config;

import com.google.common.base.Strings;
import ktest.domain.Named;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record EnvironmentConfig(String name, PresetOptions options, String onStart, String onEnd) implements Named {
    public Integer actualAutoPause(final PresetOptions pCliOptions) {
        if (pCliOptions != null && pCliOptions.autoPause != 0) {
            return pCliOptions.autoPause;
        }
        return options != null && options.autoPause != null ? pCliOptions.autoPause : 0;
    }

    public Integer actualBackOffset(final PresetOptions pCliOptions) {
        if (pCliOptions != null && pCliOptions.backOffset != 250) {
            return pCliOptions.backOffset;
        }
        return options != null && options.backOffset != null ? pCliOptions.backOffset : 250;
    }

    public String actualMatrix(final PresetOptions pCliOptions) {
        if (pCliOptions != null && !Strings.isNullOrEmpty(pCliOptions.matrix)) {
            return pCliOptions.matrix;
        }
        return options != null ? options.matrix : null;
    }

    public String actualReport(final PresetOptions pCliOptions) {
        if (pCliOptions != null && !Strings.isNullOrEmpty(pCliOptions.report)) {
            return pCliOptions.report;
        }
        return options != null ? options.report : null;
    }

    public String actualTags(final PresetOptions pCliOptions) {
        if (pCliOptions != null && !Strings.isNullOrEmpty(pCliOptions.tags)) {
            return pCliOptions.tags;
        }
        return options != null ? options.tags : null;
    }

    public List<String> onStartScript() {
        return onStart != null ? Arrays.stream(onStart.split("\n")).toList() : Collections.emptyList();
    }

    public List<String> onEndScript() {
        return onEnd != null ? Arrays.stream(onEnd.split("\n")).toList() : Collections.emptyList();
    }
}
