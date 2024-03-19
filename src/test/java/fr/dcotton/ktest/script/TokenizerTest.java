package fr.dcotton.ktest.script;

import fr.dcotton.ktest.script.token.Tokenizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TokenizerTest {
    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void letTest() {
        var res = tokenizer.tokenize("a=1").value();
        assertEquals("[Let:a, Int:1]", res.toString());

        res = tokenizer.tokenize("essai = 2.5 - 3.6").value();
        assertEquals("[Let:essai, Flt:2.5, Sub:-, Flt:3.6]", res.toString());

        res = tokenizer.tokenize("_A=5.6").value();
        assertEquals("[Let:_A, Flt:5.6]", res.toString());

        res = tokenizer.tokenize("txt=\"Liste d'éléments <=>+-/*\"").value();
        assertEquals("[Let:txt, Txt:Liste d'éléments <=>+-/*]", res.toString());
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
            tokenizer.tokenize("A_B_2==1.2 3");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected affectation\nA_B_2==1.2 3\n------^\n", e.getMessage());
        }
    }

    @Test
    void expressionTest() {
        var res = tokenizer.tokenize("(2*(3.1+4.2))/5").value();
        assertEquals("[Stm:[Int:2, Mul:*, Stm:[Flt:3.1, Add:+, Flt:4.2]], Div:/, Int:5]", res.toString());
    }

    @Test
    void functionTest() {
        var res = tokenizer.tokenize("func()").value();
        assertEquals("[Fun:func(Stm:[])]", res.toString());

        res = tokenizer.tokenize("func(1+2)").value();
        assertEquals("[Fun:func(Stm:[Stm:[Int:1, Add:+, Int:2]])]", res.toString());

        res = tokenizer.tokenize("func(1,var+3)").value();
        assertEquals("[Fun:func(Stm:[Stm:[Int:1], Stm:[Var:var, Add:+, Int:3]])]", res.toString());

        res = tokenizer.tokenize("func1(1,func2()+5)").value();
        assertEquals("[Fun:func1(Stm:[Stm:[Int:1], Stm:[Fun:func2(Stm:[]), Add:+, Int:5]])]", res.toString());

        res = tokenizer.tokenize("faker.regex(\"[A-Z]{13}\")").value();
        assertEquals("[Fun:faker.regex(Stm:[Stm:[Txt:[A-Z]{13}]])]", res.toString());

        res = tokenizer.tokenize("1+f1(2/f2(),3.2+f3(f4()))*f5(1,2.1,3)").value();
        assertEquals("[Int:1, Add:+, Fun:f1(Stm:[Stm:[Int:2, Div:/, Fun:f2(Stm:[])], Stm:[Flt:3.2, Add:+, Fun:f3(Stm:[Stm:[Fun:f4(Stm:[])]])]]), Mul:*, Fun:f5(Stm:[Stm:[Int:1], Stm:[Flt:2.1], Stm:[Int:3]])]", res.toString());
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
            tokenizer.tokenize("1.2.3").value();
            fail();
        } catch (final ScriptException e) {
            assertEquals("Unexpected char\n1.2.3\n---^\n", e.getMessage());
        }
    }
}
