package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.Deque;
import java.util.Map;

interface JsonToAvroReader {
    GenericData.Record read(Map<String, Object> json, Schema schema);

    Object read(Schema.Field field, Schema schema, Object jsonValue, Deque<String> path, boolean silently);
}