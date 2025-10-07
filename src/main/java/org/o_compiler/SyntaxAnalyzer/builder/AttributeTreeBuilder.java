package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;

public class AttributeTreeBuilder extends ClassMemberTreeBuilder implements Valuable  {
    ExpressionTreeBuilder init;
    Variable variable;
    AttributeTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
        variable = new Variable(name, type);
    }

    @Override
    public void build() {
        init = ExpressionTreeBuilder.expressionFactory(sourceCode, this);
        init.build();
    }

    @Override
    public Variable getVariable() {
        return this.variable;
    }
}
