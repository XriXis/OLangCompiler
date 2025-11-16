package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;

import java.util.ArrayList;
import java.util.List;

public class AttributeTreeBuilder extends ClassMemberTreeBuilder implements Valuable {
    ExpressionTreeBuilder init;
    Variable variable;

    AttributeTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
        variable = new Variable(name, type, this);
    }

    @Override
    public void build() {
        init = ExpressionTreeBuilder.expressionFactory(sourceCode, this);
        init.build();
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return TreeBuilder.appendTo(to, depth, parent.className + " class attribute: " + name + " with default value of: ", init == null ? new ArrayList<>() : List.of(init));
    }

    @Override
    public Variable getVariable() {
        return this.variable;
    }
}
