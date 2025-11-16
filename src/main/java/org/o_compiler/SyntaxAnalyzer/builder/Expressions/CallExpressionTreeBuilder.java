package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.SyntaxAnalyzer.builder.Classes.MethodTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

import java.util.ArrayList;

public abstract class CallExpressionTreeBuilder extends ExpressionTreeBuilder {
    // [(], ?{[arg1 expr], [,], [arg2 expr] ...}, [)]
    MethodTreeBuilder it;
    ArrayList<ExpressionTreeBuilder> args = new ArrayList<>();

    protected CallExpressionTreeBuilder(TreeBuilder parent) {
        super(parent);
    }

    @Override
    public void build() {}
}
