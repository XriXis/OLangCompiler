package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MethodCallTreeBuilder extends CallExpressionTreeBuilder {
    ExpressionTreeBuilder of;

    public MethodCallTreeBuilder(MethodTreeBuilder what, ExpressionTreeBuilder of, Iterator<Token> callSource) {
        this.it = what;
        this.of = of;
        of.parent = this;
        this.unparsedArgs = callSource;
        this.type = it.getType();
    }

    protected static MethodCallTreeBuilder initFromChain(ArrayList<ArrayList<Token>> chain, BuildTree context) {
        var res = initFromChain(
                chain,
                context,
                chain.size() - 1
        );
        res.parent = context;
        return res;
    }

    private static MethodCallTreeBuilder initFromChain(ArrayList<ArrayList<Token>> chain, BuildTree context, int cur) {
        ExpressionTreeBuilder topExpression =
                cur == 1
                        ? expressionFactory(chain.getFirst().iterator(), context)
                        : initFromChain(chain, context, cur - 1);
        if (topExpression instanceof CallExpressionTreeBuilder resWithArgs)
            for (var child : resWithArgs.args) {
                if (child==null)
                    // todo: write proper message with position identification
                    throw new CompilerError("Call of unknown method for type " + topExpression.getType());
                child.parent = topExpression;
            }
        if (!(chain.get(cur).getFirst().entry() instanceof Identifier))
            throw new CompilerError("Unexpected token met:" + chain.get(cur).getFirst() + ". Method identifier expected");
        var methodName = chain.get(cur).getFirst().entry().value();
        var method = topExpression.type.getMethodByName(methodName);
        if (method == null)
            throw new CompilerError("Class " + topExpression.type + " does not define method " + methodName + ". Undefined call found at " + chain.get(cur).getFirst().position());
        return new MethodCallTreeBuilder(method, topExpression, chain.get(cur).subList(1, chain.get(cur).size()).iterator());
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        var fullArgs = new ArrayList<>(List.of(of));
        fullArgs.addAll(args);
        return BuildTree.appendTo(to, depth, it + " call", fullArgs);
    }
}
