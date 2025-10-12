package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

import java.util.Iterator;
import java.util.List;

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
        if (!res.entry().equals(Keyword.RETURN))
            throw new InternalCommunicationError("Attempt to parse return statement, that not starts with \"return\" at " + res.position());
        result = ExpressionTreeBuilder.expressionFactory(code, this);
        validate(res);
    }

    @Override
    public BuildTree getParent() {
        return parent;
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, "Return statement", List.of(result));
    }

    private void validate(Token sign){
        var queuedParent = parent;
        while (true){
            if (queuedParent==null)
                throw new CompilerError("Wrong place for return statement. Could be written only inside methods. Found at " + sign.position());
            if (!(queuedParent instanceof MethodTreeBuilder)) {
                queuedParent = queuedParent.getParent();
                continue;
            }
            if (((MethodTreeBuilder)queuedParent).getType().equals(result.getType()))
                break;
            throw new CompilerError("Return of improper type. " + queuedParent + " of type " + ((MethodTreeBuilder) queuedParent).getType() + " cannot returns value of type " + result.getType());
        }
    }
}
