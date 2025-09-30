package org.o_compiler.SyntaxAnalyzer.tree;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.util.HashMap;

public class RootTree {
    String name;
    Iterable<Token> source_code;
    HashMap<String, ClassTree> classes;

    public RootTree(String filename, Iterable<Token> source_code) {
        name = filename;
        classes = new HashMap<>();
        this.source_code = source_code;
    }
}
