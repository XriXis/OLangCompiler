package org.o_compiler.SyntaxAnalyzer.builder;

public class Variable implements Valuable, BuildTree {
    String name;
    ClassTreeBuilder type;

    public Variable(String name, ClassTreeBuilder type) {
        this.name = name;
        this.type = type;
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
