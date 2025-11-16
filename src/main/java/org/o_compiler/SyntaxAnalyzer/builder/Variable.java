package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

public class Variable implements Valuable, TreeBuilder {
    String name;
    ClassTreeBuilder type;
    Token polymorphicIdentifier;
    TreeBuilder parent;

    public Variable(String name, ClassTreeBuilder type, TreeBuilder parent) {
        this.name = name;
        this.type = type;
        this.polymorphicIdentifier = null;
        this.parent = parent;
    }

    public Variable(String name, ClassTreeBuilder type, Token polymorphicIdentifier) {
        this.name = name;
        this.type = type;
        this.polymorphicIdentifier = polymorphicIdentifier;
    }

    public ClassTreeBuilder getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    @Override
    public Variable getVariable() {
        return this;
    }

    @Override
    public void build() {
    }

    @Override
    public TreeBuilder getParent() {
        return parent;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return null;
    }
}
