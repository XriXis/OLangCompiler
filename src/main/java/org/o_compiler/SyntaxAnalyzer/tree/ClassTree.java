package org.o_compiler.SyntaxAnalyzer.tree;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.util.HashMap;

public class ClassTree {
    String name;
    RootTree parent;
    Iterable<Token> source_code;
    HashMap<String, ClassMemberTree> class_members;

    public ClassTree(
            String class_name,
            RootTree class_parent,
            Iterable<Token> source_code
    ) {
        name = class_name;
        parent = class_parent;
        this.source_code = source_code;
        class_members = new HashMap<>();
    }
}
