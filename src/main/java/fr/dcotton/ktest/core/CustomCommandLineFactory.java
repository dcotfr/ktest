package fr.dcotton.ktest.core;

import fr.dcotton.ktest.TestFailureOrError;
import io.quarkus.picocli.runtime.PicocliCommandLineFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import picocli.CommandLine;

import java.io.PrintWriter;

import static fr.dcotton.ktest.core.AnsiColor.RED;

@ApplicationScoped
class CustomCommandLineFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CustomCommandLineFactory.class);

    @Produces
    CommandLine customCommandLine(final PicocliCommandLineFactory pFactory) {
        return pFactory
                .create()
                .setOut(new PrintWriter(new LogOutputStream(Level.INFO), true))
                .setErr(new PrintWriter(new LogOutputStream(Level.ERROR), true))
                .setExecutionExceptionHandler((e, commandLine, parseResult) -> {
                    if (e instanceof KTestException knownException) {
                        LOG.error("{}{}", RED, knownException.getMessage());
                        if (!(knownException instanceof TestFailureOrError)) {
                            LOG.debug("{}Internal trace", RED, knownException);
                        }
                        return 1;
                    }
                    LOG.error("{}Unexpected exception.", RED, e);
                    return 2;
                });
    }
}
