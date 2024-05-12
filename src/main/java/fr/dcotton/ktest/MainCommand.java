package fr.dcotton.ktest;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.annotations.RegisterForReflection;
import picocli.CommandLine;

import static fr.dcotton.ktest.MainCommand.VERSION;

@TopCommand
@CommandLine.Command(name = "ktest", description = "Kafka test utility.",
        mixinStandardHelpOptions = true, version = VERSION,
        subcommands = {RunCommand.class, DocCommand.class})
@RegisterForReflection(classNames = {
        "org.apache.kafka.common.security.scram.ScramLoginModule",
        "org.apache.kafka.common.security.scram.internals.ScramSaslClient",
        "org.apache.kafka.common.security.scram.internals.ScramSaslClient$ScramSaslClientFactory",
        "io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde",
        "sun.security.provider.ConfigFile"})
public class MainCommand {
    public static final String VERSION = "ktest v1.0.0";
}
