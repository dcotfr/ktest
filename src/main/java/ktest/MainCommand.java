package ktest;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.annotations.RegisterForReflection;
import picocli.CommandLine;

import static ktest.MainCommand.VERSION;

@TopCommand
@CommandLine.Command(name = "ktest", description = "Kafka testing utility.",
        mixinStandardHelpOptions = true, version = VERSION,
        subcommands = {SRunCommand.class, PRunCommand.class, DocCommand.class, EvalCommand.class})
@RegisterForReflection(classNames = {"org.apache.kafka.common.security.scram.ScramLoginModule",
        "org.apache.kafka.common.security.scram.internals.ScramSaslClient",
        "org.apache.kafka.common.security.scram.internals.ScramSaslClient$ScramSaslClientFactory",
        "sun.security.provider.ConfigFile"})
public class MainCommand {
    public static final String VERSION = "ktest v1.0.8";
}
