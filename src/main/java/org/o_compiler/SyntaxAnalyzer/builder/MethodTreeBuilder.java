package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;

import java.util.ArrayList;


public class MethodTreeBuilder extends ClassMemberTreeBuilder {
    // one more place to use type of "variable" (look at "to do" block in @AssignmentBuilder class)
    // todo: look at @AssignmentBuilder and use same thing here
    ArrayList<ArrayList<Object>> parameters;
    BodyTreeBuilder body;

    MethodTreeBuilder(String name, ClassTreeBuilder type, ArrayList<ArrayList<Object>> parameters, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
        this.parameters = parameters;
        body = new BodyTreeBuilder(sourceCode, this);
    }

    @Override
    public void build() {
        body.build();
    }

    // todo: enclosed names access (parameters are names inside method namespace)
}
