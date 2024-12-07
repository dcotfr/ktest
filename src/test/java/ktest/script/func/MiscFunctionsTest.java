package ktest.script.func;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.kafka.FoundRecord;
import ktest.script.Engine;
import ktest.script.ScriptException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.TimestampType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MiscFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void unknownFunctionTest() {
        try {
            engine.eval("plouf()");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: unknown function in >>>plouf<<<", e.getMessage());
        }
    }

    @Test
    void invalidNumberOfArg() {
        try {
            engine.eval("uuid(1)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid number of arguments in uuid: 0 expected, 1 found.", e.getMessage());
        }
    }

    @Test
    void invalidTypeOfArg() {
        try {
            engine.eval("regexgen(1)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid type of argument in regexgen: class java.lang.String expected, class java.lang.Long found.", e.getMessage());
        }
    }

    @Test
    void pauseTest() {
        var before = System.currentTimeMillis();
        engine.eval("pause(2000)");
        assertTrue(System.currentTimeMillis() - before >= 2000);

        before = System.currentTimeMillis();
        engine.context().disablePause(true);
        engine.eval("pause(5000)");
        assertTrue(System.currentTimeMillis() - before <= 1000);
    }

    @Test
    void envTest() {
        assertEquals(System.getenv("JAVA_HOME"), engine.eval("env(\"JAVA_HOME\")"));
        assertEquals("", engine.eval("env(\"DoesNotExistEnvVariable\")"));
    }

    @Test
    void recordTest() {
        final var cr = new ConsumerRecord<>("Top", 1, 9876L, 17766439804L, TimestampType.CREATE_TIME, 3, 3, "KEY", "VAL", new RecordHeaders(), null);
        engine.context().lastRecord(new FoundRecord(cr));
        assertEquals("{\"topic\":\"Top\",\"partition\":1,\"offset\":9876,\"timestamp\":17766439804,\"keySize\":3,\"valueSize\":3,\"headers\":{},\"key\":\"KEY\",\"value\":\"VAL\"}", engine.eval("record()"));
    }

    @Test
    void jqTest() {
        final var cr = new ConsumerRecord<>("T", 0, 1L, 2L, TimestampType.CREATE_TIME, 0, 0,
                "KEY", "{\"a\":{\"b\":25.2,\"c\":\"txt\"}}", new RecordHeaders(), null);
        engine.context().lastRecord(new FoundRecord(cr));
        assertEquals("T", engine.eval("jq(record(),\"topic\")"));
        assertEquals(0L, engine.eval("jq(record(),\"/partition\")"));
        assertEquals(1L, engine.eval("jq(record(),\"/offset\")"));
        assertEquals(2L, engine.eval("jq(record(),\"/timestamp\")"));
        assertEquals("KEY", engine.eval("jq(record(),\"/key\")"));
        assertEquals(25.2, engine.eval("jq(record(),\"/value/a/b\")"));
        assertEquals("txt", engine.eval("jq(record(),\"/value/a/c\")"));

        assertEquals("", engine.eval("jq(record(),\"/notfound\")"));

        try {
            engine.eval("jq(\"notjson\",\"/notfound\")");
            fail("Should have throw");
        } catch (final ScriptException e) {
            assertEquals("jq requires a valid json string as first argument.", e.getMessage());
        }
    }
}
