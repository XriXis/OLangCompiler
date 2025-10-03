package org.o_compiler.SyntaxAnalyzer.builder;

public interface BuildTree {
    void build();

    boolean encloseName(String name);

    BuildTree getParent();

    default ClassTreeBuilder getClass(String name) {
        BuildTree current = this;
        while (!(this.getParent() instanceof RootTreeBuilder)) {
            current = current.getParent();
        }
        return current.getParent().getClass(name);
    }
}
