package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

import java.util.ArrayList;
import java.util.Iterator;

public class ReturnStatementBuilder implements BuildTree {
    Iterator<Token> code;
    ArrayList<BuildTree> children;
    BuildTree parent;

    public ReturnStatementBuilder(Iterable<Token> source, BuildTree parent){
        code = source.iterator();
        this.parent = parent;
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
