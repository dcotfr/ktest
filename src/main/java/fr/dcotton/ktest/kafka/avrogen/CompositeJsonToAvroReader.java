package fr.dcotton.ktest.kafka.avrogen;

import io.quarkus.arc.All;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.*;

@ApplicationScoped
final class CompositeJsonToAvroReader implements JsonToAvroReader {
    private final List<AvroTypeConverter> converters = new ArrayList<>();
    private final AvroTypeConverter mainRecordConverter;

    @Inject
    @All
    private List<AvroTypeConverter> foundConverters;

    CompositeJsonToAvroReader() {
        mainRecordConverter = new RecordConverter(this);
    }

    @PostConstruct
    void init() {
        converters.add(mainRecordConverter);
        converters.addAll(foundConverters);
    }

    @Override
    public GenericData.Record read(final Map<String, Object> pJson, final Schema pSchema) {
        return (GenericData.Record) mainRecordConverter.convert(null, pSchema, pJson, new ArrayDeque<>());
    }

    @Override
    public Object read(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath) {
        final var pushed = !pField.name().equals(pPath.peekLast());
        if (pushed) {
            pPath.addLast(pField.name());
        }

        final var converter = converters.stream()
                .filter(c -> c.canManage(pSchema, pPath))
                .findFirst()
                .orElseThrow(() -> new AvroTypeException("Unsupported type: " + pField.schema().getType()));
        final var result = converter.convert(pField, pSchema, pJsonValue, pPath);

        if (pushed) {
            pPath.removeLast();
        }
        return result;
    }
}