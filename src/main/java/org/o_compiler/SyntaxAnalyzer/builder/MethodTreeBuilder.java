package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

public class MethodTreeBuilder extends ClassMemberTreeBuilder {

    MethodTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
    }

    @Override
    public void build() {

    }
}
