package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

public class ElseBlock extends BlockBuilder{
    public ElseBlock(Iterable<Token> source, TreeBuilder parent) {
        super(source, parent);
    }

    @Override
    protected void visitSingly(BuildTreeVisitor v) {
        v.visitElse(this);
    }
}
