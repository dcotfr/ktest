package fr.dcotton.ktest;

import fr.dcotton.ktest.core.KTestException;
import fr.dcotton.ktest.domain.xunit.XUnitReport;

public final class TestFailureOrError extends KTestException {
    TestFailureOrError(final XUnitReport pReport) {
        super("Ends with " + pReport.failures() + " failures and " + pReport.errors() + " errors.", null);
    }
}