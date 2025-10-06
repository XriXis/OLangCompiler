package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

public class VariableValueAccessTreeBuild extends ExpressionTreeBuilder {
    BuildTree val;
    public VariableValueAccessTreeBuild(BuildTree val, BuildTree parent) {
        this.val = val;
        this.parent = parent;
    }

    @Override
    public void build() {}
}
