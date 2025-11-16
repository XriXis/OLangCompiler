package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

import java.util.Collection;
import java.util.List;

public class EmptyExpression  extends ExpressionTreeBuilder {
    public EmptyExpression(TreeBuilder p){
        super(p);
        type = getClass("Void");
    }

    @Override
    public void build() {
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return to;
    }

    @Override
    protected void visitSingly(BuildTreeVisitor v) {
        v.visitEmptyExpression(this);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return List.of();
    }
}
