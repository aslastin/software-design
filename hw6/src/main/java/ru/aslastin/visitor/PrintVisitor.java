package ru.aslastin.visitor;

import ru.aslastin.token.Brace;
import ru.aslastin.token.NumberToken;
import ru.aslastin.token.Operation;
import ru.aslastin.token.Token;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.List;

public class PrintVisitor implements TokenVisitor {
    private final OutputStream outputStream;

    public PrintVisitor(OutputStream outputStream) {
        this.outputStream = outputStream;
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
            }
        }
    }

    @Override
    public void visit(NumberToken token) {
        writeWithWhiteSpace(Integer.toString(token.getNumber()));
    }

    @Override
    public void visit(Brace token) {
        char braceSymbol = switch (token) {
            case LEFT -> '(';
            case RIGHT -> ')';
        };
        writeWithWhiteSpace(braceSymbol);
    }

    @Override
    public void visit(Operation token) {
        char operationSymbol = switch (token) {
            case PLUS -> '+';
            case MINUS -> '-';
            case MUL -> '*';
            case DIV -> '/';
        };
        writeWithWhiteSpace(operationSymbol);
    }

    private void write(char c) {
        try {
            outputStream.write(c);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeWithWhiteSpace(char c) {
        write(c);
        write(' ');
    }

    private void write(String string) {
        for (int index = 0; index < string.length(); ++index) {
            write(string.charAt(index));
        }
    }

    private void writeWithWhiteSpace(String string) {
        write(string);
        write(' ');
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
