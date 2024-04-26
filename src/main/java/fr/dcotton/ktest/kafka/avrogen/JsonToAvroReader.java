package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.Deque;
import java.util.Map;

interface JsonToAvroReader {
    GenericData.Record read(final Map<String, Object> pJson, final Schema pSchema);

    Object read(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath);
}