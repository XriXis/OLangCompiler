package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

public class IfTreeBuilder extends ConditionalBlock {
    public IfTreeBuilder(Iterable<Token> source, BuildTree parent) {
        super(source, parent);
    }

    @Override
    protected boolean isProperOpen(TokenValue t) {
        return t.equals(Keyword.IF);
    }

    @Override
    protected boolean isStartBlock(TokenValue t) {
        return t.equals(Keyword.THEN);
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return super.appendTo(to, depth, "If block");
    }
}
