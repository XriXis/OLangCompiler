package org.o_compiler.SyntaxAnalyzer.builder.CodeBuilders;

import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.StatementScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BodyTreeBuilder extends BlockBuilder {
    Iterator<Token> code;
    ArrayList<BuildTree> children;
    HashMap<String, BuildTree> namespace;
    BuildTree parent;

    public BodyTreeBuilder(Iterable<Token> source) {
        super(source);
    }

    @Override
    public void build() {

    }

    @Override
    public boolean encloseName(String name) {
        return namespace.containsKey(name);
    }

    @Override
    public BuildTree getParent() {
        return null;
    }
}
