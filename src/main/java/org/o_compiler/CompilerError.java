package org.o_compiler;

public class CompilerError extends RuntimeException{
    public CompilerError(String msg){
        super(msg);
    }
}
