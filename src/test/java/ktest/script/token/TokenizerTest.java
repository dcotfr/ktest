package ktest.script.token;

import ktest.script.ScriptException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TokenizerTest {
    private static final Logger log = LoggerFactory.getLogger(TokenizerTest.class);
    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void letTest() {
        var res = assertOnlyOneStmAndEval("a=1");
        assertEquals("[Let:a, Int:1]", res);

        res = assertOnlyOneStmAndEval("essai = 2.5 - 3.6");
        assertEquals("[Let:essai, Flt:2.5, Sub:-, Flt:3.6]", res);

        res = assertOnlyOneStmAndEval("_A=5.6");
        assertEquals("[Let:_A, Flt:5.6]", res);

        res = assertOnlyOneStmAndEval("txt=\"Liste d'éléments <=>+-/*\"");
        assertEquals("[Let:txt, Txt:Liste d'éléments <=>+-/*]", res);
    }

    @Test
    void unexpectedLetTest() {
        try {
            tokenizer.tokenize("=1");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected affectation\n=1\n^\n", e.getMessage());
        }

        try {
            tokenizer.tokenize("1=2");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected affectation\n1=2\n-^\n", e.getMessage());
        }

        try {
            tokenizer.tokenize("A=1=3");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected affectation\nA=1=3\n---^\n", e.getMessage());
        }

        try {
            tokenizer.tokenize("1+a=2");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected affectation\n1+a=2\n---^\n", e.getMessage());
        }

        try {
            tokenizer.tokenize("A_B_2=!1.2 3");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected character\nA_B_2=!1.2 3\n------^\n", e.getMessage());
        }
    }

    @Test
    void expressionTest() {
        var res = assertOnlyOneStmAndEval("(2*(3.1+4.2))/5");
        assertEquals("[Stm:[Int:2, Mul:*, Stm:[Flt:3.1, Add:+, Flt:4.2]], Div:/, Int:5]", res);
    }

    @Test
    void functionTest() {
        var res = assertOnlyOneStmAndEval("func()");
        assertEquals("[Fun:func(Stm:[])]", res);

        res = assertOnlyOneStmAndEval("func(1+2)");
        assertEquals("[Fun:func(Stm:[Stm:[Int:1, Add:+, Int:2]])]", res);

        res = assertOnlyOneStmAndEval("func(1,var+3)");
        assertEquals("[Fun:func(Stm:[Stm:[Int:1], Stm:[Var:var, Add:+, Int:3]])]", res);

        res = assertOnlyOneStmAndEval("func1(1,func2()+5)");
        assertEquals("[Fun:func1(Stm:[Stm:[Int:1], Stm:[Fun:func2(Stm:[]), Add:+, Int:5]])]", res);

        res = assertOnlyOneStmAndEval("faker.regex(\"[A-Z]{13}\")");
        assertEquals("[Fun:faker.regex(Stm:[Stm:[Txt:[A-Z]{13}]])]", res);

        res = assertOnlyOneStmAndEval("1+f1(2/f2(),3.2+f3(f4()))*f5(1,2.1,3)");
        assertEquals("[Int:1, Add:+, Fun:f1(Stm:[Stm:[Int:2, Div:/, Fun:f2(Stm:[])], Stm:[Flt:3.2, Add:+, Fun:f3(Stm:[Stm:[Fun:f4(Stm:[])]])]]), Mul:*, Fun:f5(Stm:[Stm:[Int:1], Stm:[Flt:2.1], Stm:[Int:3]])]", res);
    }

    @Test
    void unexpectedCommaTest() {
        try {
            tokenizer.tokenize("1 , 2");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected comma\n1 , 2\n--^\n", e.getMessage());
        }
    }

    @Test
    void unexpectedRightParenthesisTest() {
        try {
            tokenizer.tokenize("var)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected right parenthesis\nvar)\n---^\n", e.getMessage());
        }
    }

    @Test
    void missingRightParenthesisTest() {
        try {
            tokenizer.tokenize("func(1+2()");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Parenthesis not matching\n(1+2()\n-----^\n", e.getMessage());
        }
    }

    @Test
    void missingDoubleQuoteTest() {
        try {
            tokenizer.tokenize("orphan \" in text");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Double quote not matching\norphan \" in text\n---------------^\n", e.getMessage());
        }
    }

    @Test
    void unexpectedCharTest() {
        try {
            tokenizer.tokenize("1.2.3").getFirst().value();
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected char\n1.2.3\n---^\n", e.getMessage());
        }
    }

    @Test
    void equalTest() {
        var res = assertOnlyOneStmAndEval("5==5");
        assertEquals("[Int:5, Eq:==, Int:5]", res);
        res = assertOnlyOneStmAndEval("VAR==0");
        assertEquals("[Var:VAR, Eq:==, Int:0]", res);
        res = assertOnlyOneStmAndEval("LOOP_COUNT==0 ? goto(\"Step n°1\")");
        assertEquals("[Stm:[Var:LOOP_COUNT, Eq:==, Int:0], If:?, Stm:[Fun:goto(Stm:[Stm:[Txt:Step n°1]])]]", res);
    }

    @Test
    void notEqualTest() {
        final var res = assertOnlyOneStmAndEval("2!=1");
        assertEquals("[Int:2, Ne:!=, Int:1]", res);
    }

    @Test
    void greaterTest() {
        final var res = assertOnlyOneStmAndEval("A>B");
        assertEquals("[Var:A, Gt:>, Var:B]", res);
    }

    @Test
    void greaterOrEqualTest() {
        final var res = assertOnlyOneStmAndEval("874 >= 900");
        assertEquals("[Int:874, Ge:>=, Int:900]", res);
    }

    @Test
    void lesserTest() {
        final var res = assertOnlyOneStmAndEval("0<0");
        assertEquals("[Int:0, Lt:<, Int:0]", res);
    }

    @Test
    void lesserOrEqualTest() {
        final var res = assertOnlyOneStmAndEval("1.1<=1.0");
        assertEquals("[Flt:1.1, Le:<=, Flt:1.0]", res);
    }

    @Test
    void ifTest() {
        var res = assertOnlyOneStmAndEval("1!=0?x=5");
        assertEquals("[Stm:[Int:1, Ne:!=, Int:0], If:?, Stm:[Let:x, Int:5]]", res);
        res = assertOnlyOneStmAndEval("0?pause(5)");
        assertEquals("[Stm:[Int:0], If:?, Stm:[Fun:pause(Stm:[Stm:[Int:5]])]]", res);
        res = assertOnlyOneStmAndEval("1==0?info(\"KO\")");
        assertEquals("[Stm:[Int:1, Eq:==, Int:0], If:?, Stm:[Fun:info(Stm:[Stm:[Txt:KO]])]]", res);
        res = assertOnlyOneStmAndEval("(1>0)?(now())");
        assertEquals("[Stm:[Stm:[Int:1, Gt:>, Int:0]], If:?, Stm:[Stm:[Fun:now(Stm:[])]]]", res);
        res = assertOnlyOneStmAndEval("1?y=6+2");
        assertEquals("[Stm:[Int:1], If:?, Stm:[Let:y, Int:6, Add:+, Int:2]]", res);
    }

    @Test
    void endOfStatementTest() {
        final var stms = tokenizer.tokenize("A=2;B=3;A*B");
        assertEquals(3, stms.size());
        assertEquals("[Let:A, Int:2]", stms.getFirst().value().toString());
        assertEquals("[Let:B, Int:3]", stms.get(1).value().toString());
        assertEquals("[Var:A, Mul:*, Var:B]", stms.getLast().value().toString());
    }

    private String assertOnlyOneStmAndEval(final String pCommand) {
        var stms = tokenizer.tokenize(pCommand);
        assertEquals(1, stms.size());
        return stms.getFirst().value().toString();
    }
}
