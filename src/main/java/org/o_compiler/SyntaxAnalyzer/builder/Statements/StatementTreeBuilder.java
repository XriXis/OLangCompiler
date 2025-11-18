package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

public abstract class StatementTreeBuilder extends TreeBuilder {
    protected StatementTreeBuilder(TreeBuilder parent) {
        super(parent);
    }
}
