package fr.dcotton.ktest;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

import static fr.dcotton.ktest.MainCommand.VERSION;

@TopCommand
@CommandLine.Command(name = "ktest", description = "Kafka test utility.",
        mixinStandardHelpOptions = true, version = VERSION,
        subcommands = {RunCommand.class, DocCommand.class})
public class MainCommand {
    public static final String VERSION = "ktest v1.0.0";
}
