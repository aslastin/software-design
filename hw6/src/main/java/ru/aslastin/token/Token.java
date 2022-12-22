package ru.aslastin.token;

import ru.aslastin.visitor.TokenVisitor;

public interface Token {
    void accept(TokenVisitor visitor);
}
