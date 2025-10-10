package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.ClassTreeBuilder;

import java.util.Iterator;

public class ConstructorInvocationTreeBuilder extends CallExpressionTreeBuilder {
    // callSource - [(], ?{[arg1 expr], [arg2 expr] ...}, [)] || empty
    public ConstructorInvocationTreeBuilder(ClassTreeBuilder type, Iterator<Token> callSource, BuildTree parent){
        this.parent = parent;
        this.type = type;
        this.unparsedArgs = callSource;
        this.it = type.getMethodByName("this");
    }

    public ConstructorInvocationTreeBuilder(ClassTreeBuilder type, Iterable<Token> callSource, BuildTree parent){
        this(type, callSource.iterator(), parent);
    }

    @Override
    public void build() {
        if (!this.unparsedArgs.hasNext()){
            // todo: identification of default constructor
            return;
        }
        super.build();
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, it + " call", args);
    }
}
