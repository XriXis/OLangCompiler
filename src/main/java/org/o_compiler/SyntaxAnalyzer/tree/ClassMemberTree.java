package org.o_compiler.SyntaxAnalyzer.tree;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

public class ClassMemberTree {
    String name;
    ClassTree parent;
    Token type;
    Iterable<Token> source_code;

    public ClassMemberTree(
            String name,
            ClassTree parent,
            Token type,
            Iterable<Token> source_code
    ) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.source_code = source_code;
    }

}
