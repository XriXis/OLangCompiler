package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.Valuable;

import java.util.List;


public class VariableValueAccessTreeBuild extends ExpressionTreeBuilder {
    Valuable val;
    public VariableValueAccessTreeBuild(Valuable val, BuildTree parent) {
        this.val = val;
        this.parent = parent;
        this.type = val.getVariable().getType();
    }

    @Override
    public void build() {}

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, "Access to bound name: " + val.getVariable().toString(), List.of());
    }
}
