package ru.aslastin.tokenizer;

import ru.aslastin.token.NumberToken;

public class NumberState implements TokenizerState {

    @Override
    public boolean handle(Tokenizer tokenizer) {
        StringBuilder numberBuilder = new StringBuilder();

        while (Character.isDigit(tokenizer.getSymbol())) {
            numberBuilder.append((char) tokenizer.getSymbol());
            tokenizer.readSymbol();
        }

        int number = Integer.parseInt(numberBuilder.toString());
        tokenizer.addToken(new NumberToken(number));

        tokenizer.setState(new StartState());

        return true;
    }
}
