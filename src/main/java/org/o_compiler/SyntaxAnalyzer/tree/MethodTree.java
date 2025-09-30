package org.o_compiler.SyntaxAnalyzer.tree;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

public class MethodTree extends ClassMemberTree{
    Iterable<Token> arguments;
    InstructionTree instructions;

    public MethodTree(
            String method_name,
            ClassTree method_parent,
            Iterable<Token> arguments,
            Token return_type,
            InstructionTree instructions,
            Iterable<Token> source_code
    ) {
        super(method_name, method_parent, return_type, source_code);
        this.arguments = arguments;
        this.instructions = instructions;
    }
}
