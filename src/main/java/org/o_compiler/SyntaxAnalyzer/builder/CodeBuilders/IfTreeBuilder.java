package org.o_compiler.SyntaxAnalyzer.builder.CodeBuilders;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

import java.util.ArrayList;
import java.util.Iterator;

public class IfTreeBuilder implements BuildTree {
    Iterator<Token> code;
    ArrayList<BuildTree> children;
    BuildTree parent;

    public IfTreeBuilder(Iterable<Token> source) {
        code = source.iterator();
    }

    @Override
    public void build() {

    }

    @Override
    public boolean encloseName(String name) {
        return false;
    }

    @Override
    public BuildTree getParent() {
        return null;
    }
}
