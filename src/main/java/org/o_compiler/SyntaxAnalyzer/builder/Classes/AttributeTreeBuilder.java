package org.o_compiler.SyntaxAnalyzer.builder.Classes;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Valuable;
import org.o_compiler.SyntaxAnalyzer.builder.Variable;

import java.util.Collection;
import java.util.List;

public class AttributeTreeBuilder extends ClassMemberTreeBuilder implements Valuable {
    ExpressionTreeBuilder init;
    Variable variable;

    AttributeTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, parent, sourceCode);
        variable = new Variable(name, type, this);
    }

    AttributeTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, ClassTreeBuilder owner, Iterable<Token> sourceCode) {
        super(name, type, parent, owner, sourceCode);
        variable = new Variable(name, type, this);
    }

    @Override
    public void build() {
        init = ExpressionTreeBuilder.expressionFactory(sourceCode, this);
        init.build();
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, ((ClassTreeBuilder)parent).className + " class attribute: " + name + " with default value of: ");
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitAttribute(this);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return init == null ? List.of() : List.of(init);
    }

    @Override
    public Variable getVariable() {
        return this.variable;
    }

    @Override
    public ClassMemberTreeBuilder clone(ClassTreeBuilder owner) {
        return new AttributeTreeBuilder(name, type, (ClassTreeBuilder) parent, owner, new IteratorSingleIterableAdapter<>(sourceCode));
    }
}
