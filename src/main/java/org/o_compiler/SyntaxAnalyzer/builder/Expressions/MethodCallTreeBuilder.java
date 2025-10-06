package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

import java.util.ArrayList;
import java.util.Iterator;


public class MethodCallTreeBuilder extends CallExpressionTreeBuilder {
    ExpressionTreeBuilder of;

    private MethodCallTreeBuilder(MethodTreeBuilder what, ExpressionTreeBuilder of, Iterator<Token> callSource) {
        this.it = what;
        this.of = of;
        this.unparsedArgs = callSource;
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
        ExpressionTreeBuilder res =
                cur == 1
                        ? expressionFactory(chain.getFirst().iterator(), context)
                        : initFromChain(chain, context, cur - 1);
        if (res instanceof CallExpressionTreeBuilder resWithArgs)
            for (var child : resWithArgs.args) child.parent = res;
        if (!(chain.get(cur).getFirst().entry() instanceof Identifier))
            throw new CompilerError("Unexpected token met:" + chain.get(cur).getFirst() + ". Method identifier expected");
        var methodName = chain.get(cur).getFirst().entry().value();
        var method = res.type.getMethodByName(methodName);
        if (method == null)
            throw new CompilerError("Class " + res.type + " does not define method " + methodName + ". Undefined call found at " + chain.get(cur).getFirst().position());
        return new MethodCallTreeBuilder(method, res, chain.get(cur).subList(1, chain.size()).iterator());
    }
}
