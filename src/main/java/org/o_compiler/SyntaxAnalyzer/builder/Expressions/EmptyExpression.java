package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

public class EmptyExpression  extends ExpressionTreeBuilder {
    public EmptyExpression(TreeBuilder p){
        type = getClass("Void");
        parent = p;
    }

    @Override
    public void build() {
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return to;
    }
}
