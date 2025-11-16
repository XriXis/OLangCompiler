package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.ClassTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.EntityScanner;
import org.o_compiler.SyntaxAnalyzer.builder.Valuable;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class ExpressionTreeBuilder implements TreeBuilder {
    ClassTreeBuilder type;
    TreeBuilder parent;

    public static ExpressionTreeBuilder expressionFactory(Iterator<Token> source, TreeBuilder context) {
        var callChain = new EntityScanner(source, context).scanChain((v) -> v.equals(ControlSign.DYNAMIC_DISPATCH));
        if (callChain.size() == 1)
            return singleAccessExpressionFactory(callChain.getFirst(), context);
        return MethodCallTreeBuilder.initFromChain(callChain, context);
    }

    private static ExpressionTreeBuilder singleAccessExpressionFactory(ArrayList<Token> entry, TreeBuilder context) {
        if (entry.getFirst().entry().equals(ControlSign.PARENTHESIS_OPEN)) {
            return expressionFactory(entry.subList(1, entry.size() - 1).iterator(), context);
        }
        if (entry.getFirst().entry() instanceof Literal<?> literal) {
            if (entry.size() != 1)
                throw new CompilerError("Unknown syntax structure. Literals can be only used by value or as context of method. Error occur at unexpected token " + entry.get(1).position());
            return new LiteralAccessExpression<>(literal, context);
        }
        var val = context.findNameAbove(entry.getFirst().entry().value());
        if (val == null)
            throw new CompilerError("Attempt to access unknown name " + entry.getFirst().entry().value() + " at " + entry.getFirst().position());
        if (val instanceof ClassTreeBuilder cls)
            return new ConstructorInvocationTreeBuilder(cls, entry.subList(1, entry.size()), context);
        if (entry.size() != 1)
            throw new CompilerError("Unknown syntax structure. Variable can be only used by value or as context of method. Error occur at unexpected token " + entry.get(1).position());
        // todo: bet rid of this crutch
        if (entry.getFirst().entry().value().equals("this")){
            var p = context;
            while (!(p instanceof ClassTreeBuilder cls)) p = p.getParent();
            return new VariableValueAccessTreeBuild(cls::getCurInstance, context);
        }
        if (val instanceof Valuable)
            return new VariableValueAccessTreeBuild((Valuable) val, context);
        throw new InternalCommunicationError("Unknown type of single term expression at " + entry.getFirst().position());
    }

    public ClassTreeBuilder getType() {
        return type;
    }

    @Override
    public TreeBuilder getParent() {
        return parent;
    }
}
