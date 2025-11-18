package org.o_compiler.SyntaxAnalyzer.builder.Classes;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.Optional;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class MethodTreeBuilder extends ClassMemberTreeBuilder {
    ArrayList<Variable> parameters;
    BodyTreeBuilder body;

    MethodTreeBuilder(String name, ClassTreeBuilder type, ArrayList<Variable> parameters, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(name, type, parent, parent, sourceCode);
        this.parameters = parameters;
        body = new BodyTreeBuilder(sourceCode, this);
    }

    MethodTreeBuilder(String name, ClassTreeBuilder type, ArrayList<Variable> parameters, ClassTreeBuilder parent, ClassTreeBuilder owner, Iterable<Token> sourceCode) {
        super(name, type, parent, owner, sourceCode);
        this.parameters = parameters;
        body = new BodyTreeBuilder(sourceCode, this);
    }

    // todo: get rid of it
    public boolean isConstructor() {
        return name.equals("this");
    }

    @Override
    public void build() {
        body.build();
        if (isConstructor())
            body.predefine(
                    (parent).children().stream()
                            .takeWhile((cm) -> cm instanceof AttributeTreeBuilder)
                            .map((attr) ->
                                    (new AssignmentBuilder(
                                            (AttributeTreeBuilder) attr,
                                            ((AttributeTreeBuilder) attr).init,
                                            this))
                            )
                            .toList()
            );
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
    public ClassMemberTreeBuilder clone(ClassTreeBuilder owner) {
        return new MethodTreeBuilder(name, type, parameters, (ClassTreeBuilder) parent, owner, new IteratorSingleIterableAdapter<>(sourceCode));
    }

    @Override
    public boolean encloseName(String name) {
        return parameters.stream().map(Variable::getName).toList().contains(name);
    }

    @Override
    public TreeBuilder getEnclosedName(String name) {
        for (Variable parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, toString());
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitMethod(this);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return List.of(body);
    }

    @Override
    public String toString() {
        if (name.equals("this")) {
            return ((ClassTreeBuilder) parent).className + " constructor(" + parameters.stream().map(v -> new Optional<>(v.getType()).map(ClassTreeBuilder::simpleName).get()).collect(Collectors.joining(", ")) + ")";
        }
        return ((ClassTreeBuilder) parent).className + " class method " + name + " (" + parameters.stream().map(v -> new Optional<>(v.getType()).map(ClassTreeBuilder::simpleName).get()).collect(Collectors.joining(", ")) + ")->" + new Optional<>(type).map(ClassTreeBuilder::simpleName);
    }

    public ArrayList<Variable> getParameters() {
        return this.parameters;
    }

    public String wasmName() {
        ArrayList<String> types = new ArrayList<>();
        for (Variable variable : this.getParameters()) {
            String typeStr = variable.getType() == null ?
                    variable.getPolymorphicIdentifier() :
                    variable.getType().simpleName();
            types.add(typeStr);
        }
        return owner.simpleName() + "_" + name + "_" + String.join("_", types);
    }
}
