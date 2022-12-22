package ru.aslastin.token;

import ru.aslastin.visitor.TokenVisitor;

public final class NumberToken implements Token {
    private final int number;

    public NumberToken(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != NumberToken.class) {
            return false;
        }
        return number == ((NumberToken) obj).number;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }
}
