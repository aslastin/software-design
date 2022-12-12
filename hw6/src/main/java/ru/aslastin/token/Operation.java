package ru.aslastin.token;

import ru.aslastin.visitor.TokenVisitor;

public enum Operation implements Token {
    PLUS(0), MINUS(0), MUL(1), DIV(1);

    private final int priority;

    Operation(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }
}
