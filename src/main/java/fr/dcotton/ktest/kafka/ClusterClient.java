package fr.dcotton.ktest.kafka;

import fr.dcotton.ktest.core.KTestException;
import fr.dcotton.ktest.domain.TestRecord;
import fr.dcotton.ktest.domain.config.KTestConfig;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static fr.dcotton.ktest.core.AnsiColor.BLUE;

@RegisterForReflection(registerFullHierarchy = true)
@ApplicationScoped
public class ClusterClient {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterClient.class);

    private final Map<String, KafkaProducer> producers = new HashMap<>();
    private final KTestConfig kConfig;
    private final KafkaConfigProvider kafkaConfigProvider;
    private final RegistryService registryService;

    @Inject
    public ClusterClient(final KTestConfig pConfig, final KafkaConfigProvider pKafkaConfigProvider, final RegistryService pRegistryService) {
        kConfig = pConfig;
        kafkaConfigProvider = pKafkaConfigProvider;
        registryService = pRegistryService;
    }

    public void send(final TopicRef pTopic, final TestRecord pRecord) {
        final var producer = producer(pTopic);
        LOG.trace("{}      Sending record to {}.", BLUE, pTopic.id());

        registryService.lastActiveSchema(pTopic, true);
        registryService.lastActiveSchema(pTopic, false);
        final var rec = new ProducerRecord<>(pTopic.topic(),
                null,
                pRecord.timestamp(),
                pRecord.keyNode() != null ? pRecord.keyNode().toString() : null,
                pRecord.valueNode() != null ? pRecord.valueNode().toString() : null,
                kafkaHeaders(pRecord.headers()));
        try {
            producer.send(rec).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KTestException("Failed to send record to " + pTopic.id(), e);
        }
    }

    private KafkaProducer producer(final TopicRef pTopic) {
        return producers.computeIfAbsent(pTopic.id(), k -> {
            final var kafkaConfig = kafkaConfigProvider.of(pTopic);
            LOG.trace("{}      Creating new producer for {}({}).", BLUE, pTopic.id(), kafkaConfig.get("bootstrap.servers"));
            final var props = new Properties();
            props.putAll(kafkaConfig);
            return new KafkaProducer<>(props);
        });
    }

    private List<Header> kafkaHeaders(final Map<String, String> pHeaders) {
        return pHeaders
                .entrySet()
                .stream()
                .map(e -> new RecordHeader(e.getKey(), e.getValue().getBytes(StandardCharsets.UTF_8)))
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    private static GenericRecord toAvroRecord(final String pJson, final Schema pSchema) {
        try (final var in = new ByteArrayInputStream(pJson.getBytes());
             final var din = new DataInputStream(in)) {
            final var jsonDecoder = DecoderFactory.get().jsonDecoder(pSchema, din);
            final var jsonReader = new GenericDatumReader<>(pSchema);
            final var jsonDatum = jsonReader.read(null, jsonDecoder);
            final var genericWriter = new GenericDatumWriter<>(pSchema);
            try (final var outputStream = new ByteArrayOutputStream()) {
                final var binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);
                genericWriter.write(jsonDatum, binaryEncoder);
                binaryEncoder.flush();
                final var avroByteArray = outputStream.toByteArray();
                final var avroReader = new GenericDatumReader<GenericRecord>(pSchema);
                final var binaryDecoder = DecoderFactory.get().binaryDecoder(avroByteArray, null);
                final var res = avroReader.read(null, binaryDecoder);
                return res;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
