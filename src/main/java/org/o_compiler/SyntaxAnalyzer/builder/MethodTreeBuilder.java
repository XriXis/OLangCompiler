package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;

import java.util.ArrayList;
import java.util.HashMap;


public class MethodTreeBuilder extends ClassMemberTreeBuilder implements BuildTree {
    HashMap<String, Variable> parameters;
    BodyTreeBuilder body;

    MethodTreeBuilder(String name, ClassTreeBuilder type, HashMap<String, Variable> parameters, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
        this.parameters = parameters;
        body = new BodyTreeBuilder(sourceCode, this);
    }

    @Override
    public void build() {
        body.build();
    }

    @Override
    public BuildTree getEnclosedName(String name) {
        return parameters.get(name);
    }
}
