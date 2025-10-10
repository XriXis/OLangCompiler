package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.EntityScanner;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.MethodCallTreeBuilder;

import java.util.ArrayList;
import java.util.Collections;

public abstract class ConditionalBlock extends BlockBuilder {
    ExpressionTreeBuilder condition;
    BlockBuilder elseBranch;

    public ConditionalBlock(Iterable<Token> source, BuildTree parent) {
        super(source, parent);
    }

    protected abstract boolean isProperOpen(TokenValue t);

    protected abstract boolean isStartBlock(TokenValue t);

    @Override
    public void build() {
        condition = parseHead();
        // todo: transfer names of base classes into global constants
        var boolCastMethod = condition.getType().getMethodByName("asBool");
        System.out.println();
        if (condition.getType() != getClass("Boolean") && boolCastMethod == null)
            throw new CompilerError("Conditional block with not boolean condition at " + code.lastRead().position());
        if (condition.getType() != getClass("Boolean"))
            condition = new MethodCallTreeBuilder(boolCastMethod, condition, Collections.emptyIterator());
        var buffer = new EntityScanner(code, this).scanFreeBlock(
                (v) -> (v instanceof Keyword) && ((Keyword) v).isBlockOpen(),
                (v) -> v.equals(Keyword.END) || v.equals(Keyword.ELSE),
                (ignored) -> true //
                // stream contains blockOpen token as first item after parseHead call, so empty sequence on valid block
                // is not expected, and we need return block as fast as we found main block closure
        );
        // End of the block is not included into the scanFreeBlock result
        code.revert();

        // ugly piece code to reuse written implementation. Main block (if condition is positive) is the conditional
        // block itself
        var temp = code;
        code = new RevertibleStream<>(buffer.iterator(), 3);
        code.next();
        if (!code.next().entry().equals(ControlSign.END_LINE))
            code.revert();
        super.build();
        code = temp;

        switch (code.next().entry()) {
            case Keyword.END -> {
            }
            case Keyword.ELSE -> {
                var elseCodeBuffer = new ArrayList<Token>();
                while (code.hasNext()) elseCodeBuffer.add(code.next());
                if (!elseCodeBuffer.getLast().entry().equals(Keyword.END))
                    // todo: display whole block position
                    throw new InternalCommunicationError("Attempt to parse conditional block, that ends with no END keyword. End of parsed block " + elseCodeBuffer.getLast().position());
                elseBranch = new BlockBuilder(new IteratorSingleIterableAdapter<>(code), this);
            }
            default ->
                    throw new CompilerError("Unclosed Conditional Block from " + buffer.getFirst().position() + " up to " + buffer.getLast().position());
        }
    }

    private ExpressionTreeBuilder parseHead() {
        var open = code.next();
        if (!isProperOpen(open.entry()))
            throw new InternalCommunicationError("Attempt to parse conditional block with head token " + open.entry().value() + " at " + open.position());
        var buffer = new ArrayList<Token>();
        do {
            if (!code.hasNext())
                throw new CompilerError("Unexpected end of statement at " + open.position() + (buffer.isEmpty() ? "" : " up to " + buffer.getLast().position()));
            buffer.add(code.next());
        } while (!isStartBlock(buffer.getLast().entry()));
        code.revert();
        buffer.removeLast();
        return ExpressionTreeBuilder.expressionFactory(buffer.iterator(), this);
    }
}
