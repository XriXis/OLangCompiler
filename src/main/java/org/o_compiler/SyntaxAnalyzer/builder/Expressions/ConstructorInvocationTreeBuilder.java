package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.ClassTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.ArgsParser;

import java.util.Iterator;
import java.util.stream.Collectors;

public class ConstructorInvocationTreeBuilder extends CallExpressionTreeBuilder {
    Iterator<Token> unparsedArgs;
    // callSource - [(], ?{[arg1 expr], [arg2 expr] ...}, [)] || empty
    public ConstructorInvocationTreeBuilder(ClassTreeBuilder type, Iterator<Token> callSource, BuildTree parent){
        this.parent = parent;
        this.type = type;
        unparsedArgs = callSource;
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
        this.args = new ArgsParser(unparsedArgs, this).get();
        this.it = type.getMethod(
                "this",
                args.stream().map(ExpressionTreeBuilder::getType).collect(Collectors.toList())
        );
        super.build();
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, it + " call", args);
    }

    @Override
    public String toString(){
        return "Call to constructor of " + type.simpleName();
    }
}
