package ktest;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.runtime.annotations.RegisterForReflection;
import picocli.CommandLine;

import static ktest.MainCommand.VERSION;

@TopCommand
@CommandLine.Command(name = "ktest", description = "Kafka testing utility.",
        mixinStandardHelpOptions = true, version = VERSION,
        subcommands = {SRunCommand.class, PRunCommand.class, DocCommand.class, EvalCommand.class, ScanCommand.class})
@RegisterForReflection(classNames = {"org.apache.kafka.common.security.scram.ScramLoginModule",
        "org.apache.kafka.common.security.scram.internals.ScramSaslClient",
        "org.apache.kafka.common.security.scram.internals.ScramSaslClient$ScramSaslClientFactory",
        "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule",
        "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler",
        "org.tukaani.xz.XZInputStream",
        "io.confluent.kafka.serializers.schema.id.ConfigSchemaIdDeserializer",
        "io.confluent.kafka.serializers.schema.id.DualSchemaIdDeserializer",
        "io.confluent.kafka.serializers.schema.id.HeaderSchemaIdSerializer",
        "io.confluent.kafka.serializers.schema.id.PrefixSchemaIdDeserializer",
        "io.confluent.kafka.serializers.schema.id.PrefixSchemaIdSerializer",
        "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer",
        "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer",
        "io.confluent.kafka.serializers.subject.DefaultReferenceSubjectNameStrategy",
        "sun.security.provider.ConfigFile"})
public class MainCommand {
    public static final String VERSION = "ktest v1.0.33";

    MainCommand() {
    }
}
