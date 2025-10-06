package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.EntityScanner;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;

import java.util.ArrayList;

public abstract class ConditionalBlocks extends BlockBuilder {
    ExpressionTreeBuilder condition;
    BlockBuilder elseBranch;
    public ConditionalBlocks(Iterable<Token> source, BuildTree parent) {
        super(source, parent);
    }

    protected abstract boolean isProperOpen(TokenValue t);

    protected abstract boolean isStartBlock(TokenValue t);

    @Override
    public void build() {
        condition = parseHead();
        var buffer = new EntityScanner(code, this).scanFreeBlock(
                (v) -> (v instanceof Keyword) && ((Keyword)v).isBlockOpen(),
                (v) -> v.equals(Keyword.END) || v.equals(Keyword.ELSE),
                (v) -> true
        );
        // scanFreeBlock omit the end of the block. In our case
        code.revert();
    }

    private ExpressionTreeBuilder parseHead() {
        var open = code.next();
        if (!isProperOpen(open.entry()))
            throw new InternalCommunicationError("Attempt to parse conditional block with head token " + open.entry().value() + " at " + open.position());
        var buffer = new ArrayList<Token>();
        while (!isStartBlock(buffer.getLast().entry())) {
            if (!code.hasNext())
                throw new CompilerError("Unexpected end of statement at " + open.position() + (buffer.isEmpty() ? "" : " up to " + buffer.getLast().position()));
            buffer.add(code.next());
        }
        code.revert();
        buffer.removeLast();
        return ExpressionTreeBuilder.expressionFactory(buffer.iterator(), this);
    }
}
