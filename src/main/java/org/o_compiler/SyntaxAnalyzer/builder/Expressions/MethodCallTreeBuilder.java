package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.EmptyExpressionException;
import org.o_compiler.SyntaxAnalyzer.Exceptions.UndefinedCallError;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.ArgsParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


public class MethodCallTreeBuilder extends CallExpressionTreeBuilder {
    ExpressionTreeBuilder of;
    Identifier rawName;

    public MethodCallTreeBuilder(Identifier methodName, ExpressionTreeBuilder of, Iterator<Token> callSource, TreeBuilder parent) {
        super(parent);
        rawName = methodName;
        of.setParent(this);
        this.args = new ArgsParser(callSource, this).get();
        var argTypes = args.stream().map(ExpressionTreeBuilder::getType).collect(Collectors.toList());
        this.of = of;
        this.it = of.getType().getMethod(
                methodName.value(),
                argTypes
        );
        if (this.it == null) {
            throw new UndefinedCallError("Class " + of.type + " does not define method " + methodName.value() + "(" + String.join(", ", args.stream().map(v -> v == null ? null : v.type.simpleName()).toList()) + ")");
        }
        this.type = it.getType();
    }

    protected static MethodCallTreeBuilder initFromChain(ArrayList<ArrayList<Token>> chain, TreeBuilder context) {
        var res = initFromChain(
                chain,
                context,
                chain.size() - 1
        );
        res.parent = context;
        return res;
    }

    private static MethodCallTreeBuilder initFromChain(ArrayList<ArrayList<Token>> chain, TreeBuilder context, int cur) {
        if (cur<=0) throw new EmptyExpressionException("Empty expression found in the " + context);
        ExpressionTreeBuilder topExpression =
                cur == 1
                        ? expressionFactory(chain.getFirst().iterator(), context)
                        : initFromChain(chain, context, cur - 1);
        topExpression.build();
        if (topExpression instanceof CallExpressionTreeBuilder resWithArgs)
            for (var child : resWithArgs.args) {
                if (child == null)
                    throw new CompilerError("Call of unknown method for type " + topExpression.getType());
                child.setParent(topExpression);
            }
        if (!(chain.get(cur).getFirst().entry() instanceof Identifier methodName))
            throw new CompilerError("Unexpected token met:" + chain.get(cur).getFirst() + ". Method identifier expected");
        try {
            return new MethodCallTreeBuilder(methodName, topExpression, chain.get(cur).subList(1, chain.get(cur).size()).iterator(), context);
        } catch (UndefinedCallError e) {
            throw new UndefinedCallError(e.getMessage() + ". Undefined call found at " + chain.get(cur).getFirst().position());
        }
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, it + " call");
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitMethodCall(this);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        var fullArgs = new ArrayList<>(List.of(of));
        fullArgs.addAll(args);
        return fullArgs;
    }

    @Override
    public String toString() {
        return "Call to method " + rawName.value();
    }
}
