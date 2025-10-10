package org.o_compiler.SyntaxAnalyzer.builder;

import java.util.Collection;

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

    StringBuilder appendTo(StringBuilder to, int depth);

    default String toString_(){
        return appendTo(new StringBuilder(), 0).toString();
    }

    static StringBuilder appendTo(StringBuilder to, int depth, String header, Collection<? extends BuildTree> children){
        to.append("\t".repeat(Math.max(0, depth))).append(header).append('\n');
        for (var child: children){
            child.appendTo(to, depth + 1);
        }
        return to;
    }
}
