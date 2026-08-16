package ktest.kafka;

public enum Serde {
    AVRO("io.confluent.kafka.serializers.KafkaAvroSerializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer"),
    STRING("org.apache.kafka.common.serialization.StringSerializer", "org.apache.kafka.common.serialization.StringDeserializer"),
    BYTES("org.apache.kafka.common.serialization.BytesSerializer", "org.apache.kafka.common.serialization.BytesDeserializer"),
    JSON("io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer", "io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer"),
    PROTOBUF("io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer", "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer");

    public final String serializer;
    public final String deserializer;

    Serde(final String pSerializer, final String pDeserializer) {
        serializer = pSerializer;
        deserializer = pDeserializer;
    }
}
