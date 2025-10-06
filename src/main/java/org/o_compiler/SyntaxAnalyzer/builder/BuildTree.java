package org.o_compiler.SyntaxAnalyzer.builder;

public interface BuildTree {
    void build();

    BuildTree getParent();

    default ClassTreeBuilder getClass(String name) {
        BuildTree current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return (ClassTreeBuilder) current.getEnclosedName(name);
    }

    // should be overridden by context objects. Default implementation for not-context nodes
    default boolean encloseName(String name) {
        return false;
    }

    // should be overridden by context objects. Default implementation for not-context nodes
    default BuildTree getEnclosedName(String name) {
        return null;
    }

    // mixin method, that allows to find corresponding to name program entity in the context encapsulation structure
    default BuildTree findNameAbove(String name) {
        BuildTree current = this;
        while (current != null) {
            if (current.encloseName(name)){
                return current.getEnclosedName(name);
            }
            current = current.getParent();
        }
        return null;
    }
}
