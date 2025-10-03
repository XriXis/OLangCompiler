package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.tree.ClassMemberTree;

import java.util.Iterator;

public abstract class ClassMemberTreeBuilder implements BuildTree {
    String name;
    ClassTreeBuilder type;
    ClassTreeBuilder parent;
    Iterator<Token> sourceCode;

    ClassMemberTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.sourceCode = sourceCode.iterator();
    }

    @Override
    public boolean encloseName(String name) {
        return false;
    }

    @Override
    public BuildTree getParent() {
        return parent;
    }
}
