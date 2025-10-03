package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

public class ClassTreeBuilder implements BuildTree {
    public ClassTreeBuilder(Iterable<Token> source) {

    }

    @Override
    public void build() {

    }

    @Override
    public boolean encloseName(String name) {
        return false;
    }
}
