package ktest;

import ktest.domain.xunit.XUnitReport;

import java.util.ArrayList;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicInteger;

final class ParallelState {
    final ArrayList<StructuredTaskScope.Subtask<XUnitReport>> subTasks = new ArrayList<>();

    final AtomicInteger endedCount = new AtomicInteger();
}
