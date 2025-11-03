package org.o_compiler.SyntaxAnalyzer.Exceptions;

public class EmptyExpressionException extends CompilerError {
    public EmptyExpressionException(String message) {
        super(message);
    }
}
