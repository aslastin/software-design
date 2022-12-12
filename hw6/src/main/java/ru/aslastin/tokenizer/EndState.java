package ru.aslastin.tokenizer;

public class EndState implements TokenizerState {
    private static EndState instance;

    private EndState() {
    }

    public static EndState getInstance() {
        if (instance == null) {
            instance = new EndState();
        }
        return instance;
    }

    @Override
    public boolean handle(Tokenizer tokenizer) {
        return false;
    }
}
