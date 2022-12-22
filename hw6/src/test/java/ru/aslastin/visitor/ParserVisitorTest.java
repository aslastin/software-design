package ru.aslastin.visitor;

import org.junit.jupiter.api.Test;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Token;
import ru.aslastin.tokenizer.TokenizerTest;
import ru.aslastin.visitor.exception.ParseException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.aslastin.token.Brace.LEFT;
import static ru.aslastin.token.Brace.RIGHT;
import static ru.aslastin.token.Operation.*;

public class ParserVisitorTest {

    public static List<Token> parseExpression(String expression) {
        List<Token> tokenizedExpression = TokenizerTest.tokenizeExpression(expression);

        ParserVisitor parserVisitor = new ParserVisitor();
        parserVisitor.visitAll(tokenizedExpression);

        return parserVisitor.getParsedExpression();
    }

    @Test
    void parseEmpty() {
        assertEquals(Collections.emptyList(), parseExpression(""));
    }

    @Test
    void parseSmall() {
        var expression = "( 30 + 2) / 8";
        var expected = List.of(new NumberToken(30), new NumberToken(2), PLUS, new NumberToken(8), DIV);
        assertEquals(expected, parseExpression(expression));
    }

    @Test
    void parseBase() {
        var expression = "2 + 3 * 4";
        var expected = List.of(new NumberToken(2), new NumberToken(3), new NumberToken(4), MUL, PLUS);
        assertEquals(expected, parseExpression(expression));
    }

    @Test
    void parseBig() {
        String expression = "(23 + 10) * 5 - 3 * (32 + 5) * (10 - 4 * 5) + 8 / 2";
        var expected = List.of(new NumberToken(23), new NumberToken(10), PLUS, new NumberToken(5), MUL,
                new NumberToken(3), new NumberToken(32), new NumberToken(5), PLUS, MUL,
                new NumberToken(10), new NumberToken(4), new NumberToken(5), MUL, MINUS,
                MUL, MINUS, new NumberToken(8), new NumberToken(2), DIV, PLUS);
        assertEquals(expected, parseExpression(expression));
    }

    @Test
    void incorrectInput() {
        assertThrows(ParseException.class, () -> {
            ParserVisitor parserVisitor = new ParserVisitor();
            var incorrectInput = List.of(LEFT, new NumberToken(5), RIGHT, RIGHT);
            parserVisitor.visitAll(incorrectInput);
        });
    }
}
