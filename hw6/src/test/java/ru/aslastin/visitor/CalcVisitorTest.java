package ru.aslastin.visitor;

import org.junit.jupiter.api.Test;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Token;
import ru.aslastin.visitor.exception.CalcException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.aslastin.token.Brace.RIGHT;
import static ru.aslastin.token.Operation.PLUS;

class CalcVisitorTest {
    public static int calculateParsedExpression(List<Token> parsedExpression) {
        CalcVisitor calcVisitor = new CalcVisitor();
        calcVisitor.visitAll(parsedExpression);

        return calcVisitor.getResult();
    }

    public static int calculateExpression(String expression) {
        List<Token> parsedExpression = ParserVisitorTest.parseExpression(expression);
        return calculateParsedExpression(parsedExpression);
    }

    @Test
    void throwsEmpty() {
        assertThrows(CalcException.class, () -> calculateExpression(""));
    }

    @Test
    void calculateSmall() {
        assertEquals(4, calculateExpression("(30 + 2) / 8"));
    }

    @Test
    void calculateManyBraces() {
        assertEquals(-2, calculateExpression("((( 2 - 2 ) + 3) - (((((5))))) * 1)"));
    }

    @Test
    void calculateBig() {
        assertEquals(1279, calculateExpression("(23 + 10) * 5 - 3 * (32 + 5) * (10 - 4 * 5) + 8 / 2"));
    }

    @Test
    void throwsBrace() {
        assertThrows(CalcException.class, () -> new CalcVisitor().visit(RIGHT));
    }

    @Test
    void throwsOperation() {
        NumberToken one = new NumberToken(1);
        assertThrows(CalcException.class, () -> calculateParsedExpression(List.of(one, one, PLUS, PLUS)));
    }
}
