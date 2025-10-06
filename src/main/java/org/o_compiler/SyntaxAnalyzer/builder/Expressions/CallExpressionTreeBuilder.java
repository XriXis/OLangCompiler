package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.EntityScanner;
import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class CallExpressionTreeBuilder extends ExpressionTreeBuilder {
    // [(], ?{[arg1 expr], [,], [arg2 expr] ...}, [)]
    Iterator<Token> unparsedArgs;
    MethodTreeBuilder it;
    ArrayList<ExpressionTreeBuilder> args;

    @Override
    public void build() {
        if (!unparsedArgs.hasNext())
            // todo position identification
            throw new CompilerError("Call of " + it + " expected");
        var brace = unparsedArgs.next();
        if (!brace.entry().equals(ControlSign.PARENTHESIS_OPEN))
            throw new CompilerError("Call of " + it + " expected at " + brace.position());
        var representation = new ArrayList<Token>();
        while (unparsedArgs.hasNext()) representation.add(unparsedArgs.next());
        if (representation.getLast().entry().equals(ControlSign.PARENTHESIS_CLOSED))
            throw new CompilerError("Unclosed call expression from " + brace.position() + " to " + representation.getLast().position());
        // for unification and EntityScanner usage. Each argument could be expression, so parsing arguments one by one
        // could be considered as parsing expressions (with "(" and ")" as inner blocking and "," as end of one argument
        // expression). But for this unified expressions ends is required
        representation.set(representation.size() - 1, new Token(ControlSign.SEPARATOR, representation.getLast().position()));
        var parser = new EntityScanner(representation.iterator(), this)
                .scanChain((v)->v.equals(ControlSign.SEPARATOR));
        for (ArrayList<Token> cur: parser) {
            args.add(expressionFactory(cur.iterator(), this));
            args.getLast().build();
        }
        // todo: check @args types for correspondence to @it signature
    }
}
