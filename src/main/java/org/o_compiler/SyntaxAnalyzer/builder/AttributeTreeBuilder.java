package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;

public class AttributeTreeBuilder extends ClassMemberTreeBuilder  {
    ExpressionTreeBuilder init;
    AttributeTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
    }

    @Override
    public void build() {
        init = ExpressionTreeBuilder.expressionFactory(sourceCode, this);
        init.build();
    }
}
