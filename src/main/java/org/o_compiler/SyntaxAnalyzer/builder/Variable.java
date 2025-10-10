package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

public class Variable implements Valuable, BuildTree {
    String name;
    ClassTreeBuilder type;
    Token polymorphicIdentifier;

    public Variable(String name, ClassTreeBuilder type) {
        this.name = name;
        this.type = type;
        this.polymorphicIdentifier = null;
    }

    public Variable(String name, ClassTreeBuilder type, Token polymorphicIdentifier) {
        this.name = name;
        this.type = type;
        this.polymorphicIdentifier = polymorphicIdentifier;
    }

    @Override
    public Variable getVariable() {
        return this;
    }

    @Override
    public void build() {
    }

    @Override
    public BuildTree getParent() {
        return null;
    }
}
