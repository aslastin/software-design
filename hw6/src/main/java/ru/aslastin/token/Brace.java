package ru.aslastin.token;

import ru.aslastin.visitor.TokenVisitor;

public enum Brace implements Token {
    LEFT, RIGHT;

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }
}
