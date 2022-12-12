package ru.aslastin.visitor;

import org.junit.jupiter.api.Test;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Token;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.aslastin.token.Brace.LEFT;
import static ru.aslastin.token.Brace.RIGHT;
import static ru.aslastin.token.Operation.*;

class PrintVisitorTest {
    public static String tokensToString(List<Token> tokens) {
        OutputStream outputStream = new ByteArrayOutputStream();

        PrintVisitor printVisitor = new PrintVisitor(outputStream);
        printVisitor.visitAll(tokens);

        return outputStream.toString();
    }

    @Test
    void empty() {
        assertEquals("", tokensToString(Collections.emptyList()));
    }

    @Test
    void expression() {
        var tokens = List.of(RIGHT, new NumberToken(123), LEFT, PLUS, new NumberToken(9249), MUL, DIV, MINUS);
        assertEquals(") 123 ( + 9249 * / - ", tokensToString(tokens));
    }
}