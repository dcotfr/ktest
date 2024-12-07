package ktest.kafka;

import ktest.core.KTestException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FoundRecordTest {
    @Test
    void newFailureTest() {
        final var cr = new ConsumerRecord<>("Topic1", 0, 1000L, 17766432908L, TimestampType.CREATE_TIME, 5, 50, "Key!", "{invalidJson}", new RecordHeaders(), null);
        try {
            new FoundRecord(cr);
            fail("Expected an exception");
        } catch (final KTestException e) {
            assertEquals("Failed to create FoundRecord.", e.getMessage());
        }
    }

    @Test
    void toStringTest() {
        final var headers = new RecordHeaders(new Header[]{new RecordHeader("head1", new String("HEAD1").getBytes())});
        final var cr = new ConsumerRecord<>("Topic2", 2, 510L, 1765432908L, TimestampType.CREATE_TIME, 10, 25, "RecordKey!", "{\"name\":\"NAME\"}", headers, null);
        assertEquals("{\"topic\":\"Topic2\",\"partition\":2,\"offset\":510,\"timestamp\":1765432908,\"keySize\":10,\"valueSize\":25,\"headers\":{\"head1\":\"HEAD1\"},\"key\":\"RecordKey!\",\"value\":{\"name\":\"NAME\"}}", new FoundRecord(cr).toString());
    }
}
