package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

public class IfTreeBuilder extends ConditionalBlock {
    public IfTreeBuilder(Iterable<Token> source, TreeBuilder parent) {
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

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        // todo: adjust DRY
        var d = v.visitIf(this, condition);
        // todo: get rid of this crutch (method should not know, what deferred action is, but we know, that it is
        //  adding closing parenthesis)
        return elseBranch == null ? () -> {
            d.act();
            d.act();
        } : () -> {
            d.act();
            elseBranch.visit(v);
            d.act();
        };
    }
}
