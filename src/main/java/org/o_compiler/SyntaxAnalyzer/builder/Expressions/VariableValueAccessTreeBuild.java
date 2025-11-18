package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Valuable;

import java.util.Collection;
import java.util.List;


public class VariableValueAccessTreeBuild extends ExpressionTreeBuilder {
    Valuable val;

    public VariableValueAccessTreeBuild(Valuable val, TreeBuilder parent) {
        super(parent);
        this.val = val;
        this.type = val.getVariable().getType();
    }

    public Valuable of(){
        return val;
    }

    @Override
    public void build() {}

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, "Access to bound name: " + val.getVariable().toString());
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitVariableValueAccess(this);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return List.of();
    }
}
