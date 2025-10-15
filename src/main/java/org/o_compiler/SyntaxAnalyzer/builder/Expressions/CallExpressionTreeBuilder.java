package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

import java.util.ArrayList;

public abstract class CallExpressionTreeBuilder extends ExpressionTreeBuilder {
    // [(], ?{[arg1 expr], [,], [arg2 expr] ...}, [)]
    MethodTreeBuilder it;
    ArrayList<ExpressionTreeBuilder> args = new ArrayList<>();

    @Override
    public void build() {}
}
