package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.util.ArrayList;


public class MethodTreeBuilder extends ClassMemberTreeBuilder {
    ArrayList<ArrayList<Object>> parameters;

    MethodTreeBuilder(String name, ClassTreeBuilder type, ArrayList<ArrayList<Object>> parameters, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
        this.parameters = parameters;
    }

    @Override
    public void build() {

    }
}
