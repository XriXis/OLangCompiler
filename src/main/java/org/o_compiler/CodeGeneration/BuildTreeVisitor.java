package org.o_compiler.CodeGeneration;

import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

public interface BuildTreeVisitor {
    void visit(TreeBuilder instance);
}
