package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

public class WhileTreeBuilder extends ConditionalBlock {
    public WhileTreeBuilder(Iterable<Token> source, TreeBuilder parent) {
        super(source, parent);
    }

    @Override
    protected boolean isProperOpen(TokenValue t) {
        return t.equals(Keyword.WHILE);
    }

    @Override
    protected boolean isStartBlock(TokenValue t) {
        return t.equals(Keyword.LOOP);
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return super.appendTo(to, depth, "While block");
    }

    @Override
    protected void visitSingly(BuildTreeVisitor v) {
        v.visitWhile(this);
    }
}
