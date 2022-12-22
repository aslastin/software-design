package ru.aslastin.tokenizer;

import ru.aslastin.token.Token;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tokenizer {
    private final InputStream expressionStream;
    private int symbol;
    private int pos;

    private TokenizerState state;
    private List<Token> tokens;

    public Tokenizer(InputStream expressionStream) {
        this.expressionStream = expressionStream;
        symbol = -1;
        pos = -1;
        state = new StartState();
    }

    public List<Token> tokenizeExpression() {
        if (tokens == null) {
            tokens = new ArrayList<>();
            readSymbol();
            while (state.handle(this));
        }
        return Collections.unmodifiableList(tokens);
    }

    void setState(TokenizerState state) {
        this.state = state;
    }

    void readSymbol() {
        try {
            symbol = expressionStream.read();
        } catch (IOException e) {
            throw new TokenizerException(e);
        }
        ++pos;
    }

    int getSymbol() {
        return symbol;
    }

    int getPos() {
        return pos;
    }

    void addToken(Token token) {
        tokens.add(token);
    }
}
