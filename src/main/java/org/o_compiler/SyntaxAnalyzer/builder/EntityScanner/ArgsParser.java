package org.o_compiler.SyntaxAnalyzer.builder.EntityScanner;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.CallExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;

import java.util.ArrayList;
import java.util.Iterator;

import static org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder.expressionFactory;

public class ArgsParser extends EntityScanner {
    public ArgsParser(Iterator<Token> source, CallExpressionTreeBuilder context) {
        super(source, context);
    }

    public ArrayList<ExpressionTreeBuilder> get(){
        var args = new ArrayList<ExpressionTreeBuilder>();
        if (!source.hasNext())
            // todo position identification
            throw new CompilerError("Call of " + context + " expected");
        var brace = source.next();
        if (!brace.entry().equals(ControlSign.PARENTHESIS_OPEN))
            throw new CompilerError(context + " expected at " + brace.position());
        var representation = new ArrayList<Token>();
        while (source.hasNext()) representation.add(source.next());
        // todo: check @args types for correspondence to @it empty signature
        if (representation.isEmpty()) return new ArrayList<>();
        while (representation.getLast().entry().equals(ControlSign.END_LINE)) representation.removeLast();
        if (representation.isEmpty()) return new ArrayList<>();
        if (!representation.getLast().entry().equals(ControlSign.PARENTHESIS_CLOSED))
            throw new CompilerError("Unclosed call expression from " + brace.position() + " to " + representation.getLast().position());
        // for unification and EntityScanner usage. Each argument could be expression, so parsing arguments one by one
        // could be considered as parsing expressions (with "(" and ")" as inner blocking and "," as end of one argument
        // expression). But for this unified expressions ends is required
        representation.set(representation.size() - 1, new Token(ControlSign.SEPARATOR, representation.getLast().position()));
        var parser = new EntityScanner(representation.iterator(), context)
                .scanChain((v)->v.equals(ControlSign.SEPARATOR));
        for (ArrayList<Token> cur: parser) {
            args.add(expressionFactory(cur.iterator(), context));
            args.getLast().build();
        }
        return args;
    }
}
