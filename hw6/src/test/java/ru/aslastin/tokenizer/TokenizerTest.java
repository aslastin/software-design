package ru.aslastin.tokenizer;

import org.junit.jupiter.api.Test;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Token;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.aslastin.token.Brace.LEFT;
import static ru.aslastin.token.Brace.RIGHT;
import static ru.aslastin.token.Operation.*;

public class TokenizerTest {

    public static List<Token> tokenizeExpression(String expression) {
        InputStream expressionStream = new ByteArrayInputStream(expression.getBytes(StandardCharsets.UTF_8));
        Tokenizer tokenizer = new Tokenizer(expressionStream);
        return tokenizer.tokenizeExpression();
    }

    @Test
    void parseEmpty() {
        assertEquals(Collections.emptyList(), tokenizeExpression(""));
    }

    @Test
    void parseSmall() {
        var expression = "(30+2)/8";
        var expected = List.of(LEFT, new NumberToken(30), PLUS, new NumberToken(2), RIGHT, DIV, new NumberToken(8));
        assertEquals(expected, tokenizeExpression(expression));
    }

    @Test
    void parseWithWhiteSpaces() {
        var expression = " (  2    * 2) -  4 + 3";
        var expected = List.of(LEFT, new NumberToken(2), MUL, new NumberToken(2), RIGHT,
                MINUS, new NumberToken(4), PLUS, new NumberToken(3)
        );
        assertEquals(expected, tokenizeExpression(expression));
    }

    @Test
    void parseBig() {
        var expression = "(23 + 10) * 5 - 3 * (32 + 5) * (10 - 4 * 5) + 8 / 2";
        var expected = List.of(LEFT, new NumberToken(23), PLUS, new NumberToken(10), RIGHT,
                MUL, new NumberToken(5), MINUS, new NumberToken(3), MUL,
                LEFT, new NumberToken(32), PLUS, new NumberToken(5), RIGHT,
                MUL,
                LEFT, new NumberToken(10), MINUS, new NumberToken(4), MUL, new NumberToken(5), RIGHT,
                PLUS, new NumberToken(8), DIV, new NumberToken(2)
        );
        assertEquals(expected, tokenizeExpression(expression));
    }

    @Test
    void incorrectInput() {
        assertThrows(TokenizerException.class, () -> tokenizeExpression(" foo bar"));
    }
}
