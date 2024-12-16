package ktest;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.LogTab;
import ktest.script.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import static ktest.MainCommand.VERSION;
import static ktest.core.AnsiColor.WHITE;

@CommandLine.Command(name = "eval", description = "Evaluates a script and displays its final result.",
        mixinStandardHelpOptions = true, version = VERSION)
public class EvalCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(EvalCommand.class);

    @CommandLine.Option(names = {"-l", "--line"}, description = "In-line statement(s) to evaluate.", required = true)
    private String line;

    private final Instance<Engine> engineFactory;
    private final LogTab logTab = new LogTab(false);

    @Inject
    EvalCommand(final Instance<Engine> pEngineFactory) {
        engineFactory = pEngineFactory;
    }

    @Override
    public void run() {
        final var engine = engineFactory.get();
        var cleanLine = line;
        if (cleanLine.startsWith("\"") && cleanLine.endsWith("\"")) {
            cleanLine = cleanLine.substring(1, cleanLine.length() - 1).trim();
        }
        LOG.debug("{}InLine: {}", logTab.tab(WHITE), cleanLine);
        LOG.info("{}Result: {}", logTab.tab(WHITE), engine.eval(cleanLine));
    }
}
