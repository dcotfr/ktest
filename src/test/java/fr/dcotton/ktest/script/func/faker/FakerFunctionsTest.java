package fr.dcotton.ktest.script.func.faker;

import fr.dcotton.ktest.script.Engine;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FakerFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void regexgenTest() {
        assertEquals("AAAAAAAA", engine.eval("regexgen(\"A{8}\")"));
    }

    @Test
    void uuidTest() {
        final var pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertTrue(pattern.matcher(engine.eval("uuid()").toString()).matches());
    }
}
