package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.EmptyExpressionException;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.EmptyExpression;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.MethodTreeBuilder;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ReturnStatementBuilder extends StatementTreeBuilder {
    Iterator<Token> code;
    ExpressionTreeBuilder result;

    public ReturnStatementBuilder(Iterable<Token> source, TreeBuilder parent){
        super(parent);
        code = source.iterator();
    }

    @Override
    public void build() {
        var res = code.next();
        if (!res.entry().equals(Keyword.RETURN))
            throw new InternalCommunicationError("Attempt to parse return statement, that not starts with \"return\" at " + res.position());
        try {
            result = ExpressionTreeBuilder.expressionFactory(code, this);
        } catch (EmptyExpressionException ignored){
            result = new EmptyExpression(this);
        }
        validate(res);
        result.build();
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, "Return statement");
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitReturnStatement(this);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return List.of(result);
    }

    private void validate(Token sign){
        var queuedParent = parent;
        while (true){
            if (queuedParent==null)
                throw new CompilerError("Wrong place for return statement. Could be written only inside methods. Found at " + sign.position());
            if (!(queuedParent instanceof MethodTreeBuilder ref)) {
                queuedParent = queuedParent.getParent();
                continue;
            }
            // todo get rid of way it is done
            if (ref.isConstructor()) {
                if (result instanceof EmptyExpression) break;
                throw new CompilerError("Non empty return statement in the constructor body at " + sign.position());
            }
            if (ref.getType().equals(result.getType()))
                break;
            throw new CompilerError("Return of improper type. " + queuedParent + " of type " + ref.getType() + " cannot returns value of type " + result.getType());
        }
    }
}
