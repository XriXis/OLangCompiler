package org.o_compiler.SyntaxAnalyzer.Exceptions;

public class UnclosedBlockException extends CompilerError{
    public UnclosedBlockException(String msg) {
        super(msg);
    }
}
