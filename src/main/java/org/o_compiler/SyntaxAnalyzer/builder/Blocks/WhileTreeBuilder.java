package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

public class WhileTreeBuilder extends ConditionalBlock {
    public WhileTreeBuilder(Iterable<Token> source, BuildTree parent) {
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
}
