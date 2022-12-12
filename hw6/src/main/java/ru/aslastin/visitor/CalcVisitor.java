package ru.aslastin.visitor;

import ru.aslastin.token.Brace;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Operation;
import ru.aslastin.token.Token;
import ru.aslastin.visitor.exception.CalcException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class CalcVisitor implements TokenVisitor {
    private final Deque<Integer> stack;

    public CalcVisitor() {
        stack = new ArrayDeque<>();
    }

    @Override
    public void visitAll(List<Token> tokens) {
        for (Token token : tokens) {
            if (token instanceof NumberToken numberToken) {
                visit(numberToken);
            } else if (token instanceof Operation operation) {
                visit(operation);
            } else {
                throw new CalcException("can not calculate " + token);
            }
        }

        if (stack.size() != 1) {
            throw new CalcException("there must be single expression's result, but was " + stack.size());
        }
    }

    @Override
    public void visit(NumberToken token) {
        stack.addLast(token.getNumber());
    }

    @Override
    public void visit(Brace token) {
        throw new CalcException("can not calculate brace");
    }

    @Override
    public void visit(Operation token) {
        if (stack.size() < 2) {
            throw new CalcException("binary operation doesn't have enough arguments");
        }
        int right = stack.removeLast();
        int left = stack.removeLast();
        int result = switch (token) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case MUL -> left * right;
            case DIV -> left / right;
        };
        stack.addLast(result);
    }

    public int getResult() {
        if (stack.isEmpty()) {
            throw new CalcException("no result");
        }
        return stack.getLast();
    }
}
