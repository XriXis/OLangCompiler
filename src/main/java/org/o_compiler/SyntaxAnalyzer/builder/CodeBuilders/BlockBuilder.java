package org.o_compiler.SyntaxAnalyzer.builder.CodeBuilders;

import com.sun.source.tree.BlockTree;
import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.StatementScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class BlockBuilder implements BuildTree {
    Iterator<Token> code;
    ArrayList<BuildTree> children = new ArrayList<>();
    HashMap<String, BuildTree> namespace = new HashMap<>();
    BuildTree parent;

    public BlockBuilder(Iterable<Token> source) {
        code = source.iterator();
    }

    public void build() {
        for (var token: (new IteratorSingleIterableAdapter<>(new StatementScanner(code)))){
            children.add(token);
        }

    }

    public boolean encloseName(String name) {
        return false;
    }
}
