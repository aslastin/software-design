package ru.aslastin.tokenizer;

public class TokenizerException extends RuntimeException {
    public TokenizerException(String message) {
        super(message);
    }

    public TokenizerException(Throwable cause) {
        super(cause);
    }
}
