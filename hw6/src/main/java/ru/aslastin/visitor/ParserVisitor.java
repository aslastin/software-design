package ru.aslastin.visitor;

import ru.aslastin.token.Brace;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Operation;
import ru.aslastin.token.Token;
import ru.aslastin.visitor.exception.ParseException;

import java.util.*;

public class ParserVisitor implements TokenVisitor {
    private final Deque<Token> stack;
    private final List<Token> parsedTokens;

    public ParserVisitor() {
        stack = new ArrayDeque<>();
        parsedTokens = new ArrayList<>();
    }

    @Override
    public void visitAll(List<Token> tokens) {
        for (Token token : tokens) {
            if (token instanceof NumberToken numberToken) {
                visit(numberToken);
            } else if (token instanceof Brace brace) {
                visit(brace);
            } else if (token instanceof Operation operation) {
                visit(operation);
            } else {
                throw new ParseException("unknown token: " + token);
            }
        }

        while (!stack.isEmpty()) {
            parsedTokens.add(stack.removeLast());
        }
    }

    @Override
    public void visit(NumberToken token) {
        parsedTokens.add(token);
    }

    @Override
    public void visit(Brace token) {
        switch (token) {
            case LEFT -> stack.addLast(token);
            case RIGHT -> {
                while (!stack.isEmpty() && stack.getLast() != Brace.LEFT) {
                    parsedTokens.add(stack.removeLast());
                }

                if (stack.isEmpty()) {
                    throw new ParseException("expected left brace, but it wasn't found");
                } else {
                    stack.removeLast();
                }
            }
        }
    }

    @Override
    public void visit(Operation token) {
        while (!stack.isEmpty() &&
                (stack.getLast() instanceof Operation operation) &&
                operation.getPriority() >= token.getPriority()) {
            parsedTokens.add(stack.removeLast());
        }
        stack.addLast(token);
    }

    public List<Token> getParsedExpression() {
        return parsedTokens;
    }
}
