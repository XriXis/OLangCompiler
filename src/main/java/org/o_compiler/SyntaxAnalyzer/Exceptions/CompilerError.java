package org.o_compiler.SyntaxAnalyzer.Exceptions;

public class CompilerError extends RuntimeException{
    public CompilerError(String msg){
        super(msg);
    }
}
