package ktest.scan;

import ktest.kafka.ScanResult;

public interface ScanRenderer {
    String render(final ScanResult pResult);
}
