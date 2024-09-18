package ktest.script;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EngineCompsTest {
    private static final Logger log = LoggerFactory.getLogger(EngineCompsTest.class);
    @Inject
    private Engine engine;

    @Test
    void eqTest() {
        assertEquals(1L, engine.eval("2==2"));
        assertEquals(0L, engine.eval("5==0"));
        assertEquals(1L, engine.eval("3.14==3.14"));
        assertEquals(1L, engine.eval("\"12\"==12"));
        assertEquals(0L, engine.eval("\"AB\"==\"BC\""));
    }

    @Test
    void invalidSyntaxTest() {
        try {
            engine.eval("==2");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a token was expected in ==2", e.getMessage());
        }
        try {
            engine.eval("2>=");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a token was expected in 2>=", e.getMessage());
        }
        try {
            engine.eval("<");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a token was expected in <", e.getMessage());
        }
    }

    @Test
    void geTest() {
        assertEquals(1L, engine.eval("2>=1"));
        assertEquals(1L, engine.eval("2>=2"));
        assertEquals(0L, engine.eval("2>=3"));
        assertEquals(1L, engine.eval("5>=0"));
        assertEquals(1L, engine.eval("3.14>=3.14"));
        assertEquals(1L, engine.eval("\"12\">=12"));
        assertEquals(1L, engine.eval("\"AB\">=\"AB\""));
        assertEquals(0L, engine.eval("\"AB\">=\"BC\""));
        assertEquals(1L, engine.eval("\"BC\">=\"AB\""));
    }

    @Test
    void gtTest() {
        assertEquals(1L, engine.eval("2>1"));
        assertEquals(0L, engine.eval("2>2"));
        assertEquals(0L, engine.eval("2>3"));
        assertEquals(1L, engine.eval("5>0"));
        assertEquals(0L, engine.eval("3.14>3.14"));
        assertEquals(0L, engine.eval("\"12\">12"));
        assertEquals(0L, engine.eval("\"AB\">\"AB\""));
        assertEquals(0L, engine.eval("\"AB\">\"BC\""));
        assertEquals(1L, engine.eval("\"BC\">\"AB\""));
    }

    @Test
    void leTest() {
        assertEquals(0L, engine.eval("2<=1"));
        assertEquals(1L, engine.eval("2<=2"));
        assertEquals(1L, engine.eval("2<=3"));
        assertEquals(0L, engine.eval("5<=0"));
        assertEquals(1L, engine.eval("3.14<=3.14"));
        assertEquals(1L, engine.eval("\"12\"<=12"));
        assertEquals(1L, engine.eval("\"AB\"<=\"AB\""));
        assertEquals(1L, engine.eval("\"AB\"<=\"BC\""));
        assertEquals(0L, engine.eval("\"BC\"<=\"AB\""));
    }

    @Test
    void ltTest() {
        assertEquals(0L, engine.eval("2<1"));
        assertEquals(0L, engine.eval("2<2"));
        assertEquals(1L, engine.eval("2<3"));
        assertEquals(0L, engine.eval("5<0"));
        assertEquals(0L, engine.eval("3.14<3.14"));
        assertEquals(0L, engine.eval("\"12\"<12"));
        assertEquals(0L, engine.eval("\"AB\"<\"AB\""));
        assertEquals(1L, engine.eval("\"AB\"<\"BC\""));
        assertEquals(0L, engine.eval("\"BC\"<\"AB\""));
    }

    @Test
    void neTest() {
        assertEquals(0L, engine.eval("2!=2"));
        assertEquals(1L, engine.eval("5!=0"));
        assertEquals(0L, engine.eval("3.14!=3.14"));
        assertEquals(0L, engine.eval("\"12\"!=12"));
        assertEquals(1L, engine.eval("\"AB\"!=\"BC\""));
    }

    @Test
    void ifTest() {
        engine.eval("0==1?x=2");
        engine.eval("0<=1?y=4");
        assertNull(engine.context().variable("x"));
        assertEquals(4L, engine.context().variable("y").value());

        engine.reset().eval("1!=0?x=5");
        assertEquals(5L, engine.context().variable("x").value());

        final var start = System.currentTimeMillis();
        engine.reset().eval("1?pause(100)");
        assertTrue(System.currentTimeMillis() - start >= 100);

        engine.reset().eval("1==0?info(\"KO\")");

        assertTrue((Long) engine.reset().eval("(1>0)?(now())") >= System.currentTimeMillis());

        assertEquals(8L, engine.reset().eval("1?y=6+2"));
    }
}
