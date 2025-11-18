package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.EntityScanner;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.MethodCallTreeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ConditionalBlock extends BlockBuilder {
    ExpressionTreeBuilder condition;
    ElseBlock elseBranch;

    public ConditionalBlock(Iterable<Token> source, TreeBuilder parent) {
        super(source, parent);
    }

    @Override
    public void build() {
        condition = parseHead();
        // todo: transfer names of base classes into global constants
        // boolCastMethod just for error detection and printing
        var boolCastMethod = condition.getType().getMethod("asBool", new ArrayList<>());
        if (condition.getType() != getClass("Boolean") && boolCastMethod == null)
            throw new CompilerError("Conditional block with not boolean condition at " + code.lastRead().position());
        if (condition.getType() != getClass("Boolean"))
            condition = new MethodCallTreeBuilder(new Identifier("asBool"), condition, Collections.emptyIterator(), this);
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
                elseCodeBuffer.removeLast();
                elseBranch = new ElseBlock(elseCodeBuffer, this);
                elseBranch.build();
            }
            default ->
                    throw new CompilerError("Unclosed Conditional Block from " + buffer.getFirst().position() + " up to " + buffer.getLast().position());
        }
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth, String label) {
//        var indent = "\t".repeat(depth);
//        to.append(indent).append(label).append('\n');
//        to.append(indent).append("=====CONDITION=====\n");
        new EmptyView(this).appendTo(to, depth, "=====CONDITION=====");
//        condition.appendTo(to, depth);
        new ConditionExprView(this).appendTo(to, depth);
//        to.append(indent).append("===END-CONDITION===\n");
        new EmptyView(this).appendTo(to, depth, "===END-CONDITION===");
        super.appendTo(to, depth+1, "");
//        for (var child: children){
//            child.appendTo(to, depth+1);
//        }
        if (elseBranch!=null) {
            new ElseBlockPrintView(this).appendTo(to, depth, "Else block");
//            to.append(indent).append("Else block\n");
//            elseBranch.appendTo(to, depth + 1);
        }
        return to;
    }

    protected abstract boolean isProperOpen(TokenValue t);

    protected abstract boolean isStartBlock(TokenValue t);

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

    // #region
    protected StringBuilder originalAppendTo(StringBuilder to, int depth, String label){
        return super.appendTo(to, depth, label);
    }

    private static class PrintView extends ConditionalBlock{
        public PrintView(ConditionalBlock o){
            super(new IteratorSingleIterableAdapter<>(o.code), o.parent);
            condition = o.condition;
            children = o.children;
            namespace = o.namespace;
            elseBranch = o.elseBranch;
        }

        @Override
        public StringBuilder appendTo(StringBuilder to, int depth, String label) {
            return originalAppendTo(to, depth, label);
        }

        @Override
        protected boolean isProperOpen(TokenValue t) {
            throw new RuntimeException("Unsupposed to be used outside the ConditionalBlock class");
        }

        @Override
        protected boolean isStartBlock(TokenValue t) {
            throw new RuntimeException("Unsupposed to be used outside the ConditionalBlock class");
        }

        @Override
        protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
            throw new RuntimeException("Unsupposed to be used outside the ConditionalBlock class");
        }
    }

    private static class ConditionExprView extends PrintView {
        public ConditionExprView(ConditionalBlock o) {
            super(o);
        }

        @Override
        public Collection<? extends TreeBuilder> children(){
            return List.of(condition);
        }
    }

    private static class ElseBlockPrintView extends PrintView {
        public ElseBlockPrintView(ConditionalBlock o) {
            super(o);
        }
        @Override
        public Collection<? extends TreeBuilder> children(){
            return elseBranch.children();
        }
    }

    private static class EmptyView extends PrintView {
        public EmptyView(ConditionalBlock o) {
            super(o);
        }

        @Override
        public Collection<? extends TreeBuilder> children(){
            return List.of();
        }
    }
    // #endregion
}
