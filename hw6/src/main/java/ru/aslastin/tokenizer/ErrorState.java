package ru.aslastin.tokenizer;

public class ErrorState implements TokenizerState {
    private final char errorSymbol;
    private final int errorPos;

    public ErrorState(char errorSymbol, int errorPos) {
        this.errorSymbol = errorSymbol;
        this.errorPos = errorPos;
    }

    @Override
    public boolean handle(Tokenizer tokenizer) {
        throw new TokenizerException(String.format("unexpected symbol: %c at pos: %d", errorSymbol, errorPos));
    }
}
