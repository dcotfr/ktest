package ktest.script;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.McpMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EngineMcpModeTest {
    @Inject
    private Engine engine;

    @Inject
    private McpMode mcpMode;

    @AfterEach
    void resetMcpMode() {
        mcpMode.disable();
    }

    @Test
    void allFunctionsAvailableOutsideMcpModeTest() {
        assertEquals("x", engine.eval("info(\"x\")"));
        final var javaHome = System.getenv("JAVA_HOME");
        assertEquals(javaHome == null ? "" : javaHome, engine.eval("env(\"JAVA_HOME\")"));
    }

    @Test
    void undocumentedFunctionsAreNoOpInMcpModeTest() {
        mcpMode.enable();
        assertEquals("", engine.eval("info(\"x\")"));
        assertEquals("", engine.eval("debug(2+3)"));
        assertEquals("", engine.eval("trace(\"t\")"));
        assertEquals("", engine.eval("warn(\"w\")"));
        assertEquals("", engine.eval("error(\"Failed\")"));
        assertEquals("", engine.eval("env(\"JAVA_HOME\")"));
        assertEquals("", engine.eval("goto(\"step\")"));
    }

    @Test
    void documentedFunctionsStillWorkInMcpModeTest() {
        mcpMode.enable();
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", engine.eval("sha256(\"abc\")"));
        assertEquals("AaaBbb", engine.eval("concat(\"Aaa\",\"Bbb\")"));
        assertTrue(engine.eval("uuid()").toString()
                .matches("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
    }

    @Test
    void unknownFunctionStillFailsInMcpModeTest() {
        mcpMode.enable();
        final var e = assertThrowsExactly(ScriptException.class, () -> engine.eval("plouf()"));
        assertEquals("Syntax error: unknown function in >>>plouf<<<", e.getMessage());
    }
}
