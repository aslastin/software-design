package ru.aslastin;

import ru.aslastin.token.Token;
import ru.aslastin.tokenizer.Tokenizer;
import ru.aslastin.visitor.CalcVisitor;
import ru.aslastin.visitor.ParserVisitor;
import ru.aslastin.visitor.PrintVisitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("expected single argument: infix expression");
            return;
        }

        String expression = args[0];
        InputStream expressionStream = new ByteArrayInputStream(expression.getBytes(StandardCharsets.UTF_8));

        Tokenizer tokenizer = new Tokenizer(expressionStream);
        List<Token> tokenizedExpression = tokenizer.tokenizeExpression();

        var parserVisitor = new ParserVisitor();
        parserVisitor.visitAll(tokenizedExpression);
        List<Token> parsedExpression = parserVisitor.getParsedExpression();

        var printVisitor = new PrintVisitor(new ByteArrayOutputStream());
        printVisitor.visitAll(parsedExpression);

        var calcVisitor = new CalcVisitor();
        calcVisitor.visitAll(parsedExpression);

        System.out.println("Input expression: " + expression);
        System.out.println("Reverse Polish notation: " + printVisitor.getOutputStream().toString());
        System.out.println("Calculated result = " + calcVisitor.getResult());
    }
}
