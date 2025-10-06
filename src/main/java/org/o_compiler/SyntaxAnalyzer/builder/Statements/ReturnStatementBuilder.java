package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;

import java.util.Iterator;

public class ReturnStatementBuilder implements BuildTree {
    Iterator<Token> code;
    ExpressionTreeBuilder result;
    BuildTree parent;

    public ReturnStatementBuilder(Iterable<Token> source, BuildTree parent){
        code = source.iterator();
        this.parent = parent;
    }

    @Override
    public void build() {
        var res = code.next();
        if (res.entry().equals(Keyword.RETURN))
            throw new InternalCommunicationError("Attempt to parse return statement, that not starts with \"return\" at " + res.position());
        result = ExpressionTreeBuilder.expressionFactory(code, this);
    }

    @Override
    public BuildTree getParent() {
        return parent;
    }
}
