package ru.aslastin.visitor;

import ru.aslastin.token.Brace;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Operation;
import ru.aslastin.token.Token;

import java.util.List;

public interface TokenVisitor {
    void visitAll(List<Token> tokens);
    void visit(NumberToken token);
    void visit(Brace token);
    void visit(Operation token);
}
