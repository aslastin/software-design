package ru.aslastin.tokenizer;

import ru.aslastin.token.Brace;
import ru.aslastin.token.Operation;
import ru.aslastin.token.Token;

public class StartState implements TokenizerState {

    @Override
    public boolean handle(Tokenizer tokenizer) {
        while (Character.isWhitespace(tokenizer.getSymbol())) {
            tokenizer.readSymbol();
        }

        if (tokenizer.getSymbol() == -1) {
            tokenizer.setState(EndState.getInstance());
        } else if (Character.isDigit(tokenizer.getSymbol())) {
            tokenizer.setState(new NumberState());
        } else {
            char c = (char) tokenizer.getSymbol();
            Token token = switch (c) {
                case '(' -> Brace.LEFT;
                case ')' -> Brace.RIGHT;
                case '+' -> Operation.PLUS;
                case '-' -> Operation.MINUS;
                case '*' -> Operation.MUL;
                case '/' -> Operation.DIV;
                default -> {
                    tokenizer.setState(new ErrorState(c, tokenizer.getPos()));
                    yield null;
                }
            };
            tokenizer.addToken(token);

            tokenizer.readSymbol();
        }

        return true;
    }
}
