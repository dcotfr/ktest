package fr.dcotton.ktest.kafka.avrogen;

import fr.dcotton.ktest.script.func.Func;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.*;

@ApplicationScoped
final class CompositeJsonToAvroReader implements JsonToAvroReader {
    private final List<AvroTypeConverter> converters;
    private final AvroTypeConverter mainRecordConverter;

    @Inject
    @All
    private List<AvroTypeConverter> funcs;

    CompositeJsonToAvroReader() {
        this(Collections.emptyList());
    }

    public CompositeJsonToAvroReader(List<AvroTypeConverter> additionalConverters) {
        mainRecordConverter = createMainConverter();
        converters = new ArrayList<>();
        converters.addAll(additionalConverters);
        converters.add(BytesDecimalConverter.INSTANCE);
        converters.add(IntDateConverter.INSTANCE);
        converters.add(IntTimeMillisConverter.INSTANCE);
        converters.add(LongTimeMicrosConverter.INSTANCE);
        converters.add(LongTimestampMillisConverter.INSTANCE);
        converters.add(LongTimestampMicrosConverter.INSTANCE);
        converters.add(PrimitiveConverter.BOOLEAN);
        converters.add(PrimitiveConverter.STRING);
        converters.add(PrimitiveConverter.INT);
        converters.add(PrimitiveConverter.LONG);
        converters.add(PrimitiveConverter.DOUBLE);
        converters.add(PrimitiveConverter.FLOAT);
        converters.add(PrimitiveConverter.BYTES);
        converters.add(EnumConverter.INSTANCE);
        converters.add(NullConverter.INSTANCE);
        converters.add(mainRecordConverter);
        converters.add(new ArrayConverter(this));
        converters.add(new MapConverter(this));
        converters.add(new UnionConverter(this));
    }

    protected AvroTypeConverter createMainConverter() {
        return new RecordConverter(this);
    }

    @Override
    public GenericData.Record read(Map<String, Object> json, Schema schema) {
        return (GenericData.Record) this.mainRecordConverter.convert(null, schema, json, new ArrayDeque<>());
    }

    @Override
    public Object read(Schema.Field field, Schema schema, Object jsonValue, Deque<String> path, boolean silently) {
        boolean pushed = !field.name().equals(path.peekLast());
        if (pushed) {
            path.addLast(field.name());
        }

        AvroTypeConverter converter = this.converters.stream()
                .filter(c -> c.canManage(schema, path))
                .findFirst()
                .orElseThrow(() -> new AvroTypeException("Unsupported type: " + field.schema().getType()));
        Object result = converter.convert(field, schema, jsonValue, path);

        if (pushed) {
            path.removeLast();
        }
        return result;
    }
}