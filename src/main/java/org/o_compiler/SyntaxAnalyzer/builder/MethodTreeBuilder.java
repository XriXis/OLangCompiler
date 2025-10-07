package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;

import java.util.ArrayList;
import java.util.HashMap;


public class MethodTreeBuilder extends ClassMemberTreeBuilder implements BuildTree {
    ArrayList<Variable> parameters;
    BodyTreeBuilder body;

    MethodTreeBuilder(String name, ClassTreeBuilder type, ArrayList<Variable> parameters, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
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
        for (Variable parameter : parameters) {
            if (parameter.name.equals(name)) {
                return parameter;
            }
        }
        return null;
    }
}
