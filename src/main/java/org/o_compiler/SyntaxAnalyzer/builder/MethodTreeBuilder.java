package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.Optional;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;

import java.util.ArrayList;
import java.util.List;


public class MethodTreeBuilder extends ClassMemberTreeBuilder implements BuildTree {
    ArrayList<Variable> parameters;
    BodyTreeBuilder body;

    MethodTreeBuilder(String name, ClassTreeBuilder type, ArrayList<Variable> parameters, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, sourceCode);
        this.parameters = parameters;
        body = new BodyTreeBuilder(sourceCode, this);
    }

    public ClassTreeBuilder getType(){
        return this.type;
    }

    @Override
    public void build() {
        body.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodTreeBuilder methodObj) {
            if (methodObj.name.equals(this.name) && methodObj.type.equals(this.type)) {
                var params = methodObj.parameters;
                if (params.size() != parameters.size())
                    return false;
                for (int i = 0; i < parameters.size(); i++) {
                    if (!parameters.get(i).equals(params.get(i)))
                        return false;
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
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

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, toString(), List.of(body));
    }

    @Override
    public String toString(){
        return parent.className + " class method " + name + " (" + new Optional<>(getType()).map(ClassTreeBuilder::simpleName) + ")";
    }
}
