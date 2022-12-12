package ru.aslastin.visitor;

import ru.aslastin.token.Brace;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Operation;

public interface TokenVisitor {
    void visit(NumberToken token);
    void visit(Brace token);
    void visit(Operation token);
}
