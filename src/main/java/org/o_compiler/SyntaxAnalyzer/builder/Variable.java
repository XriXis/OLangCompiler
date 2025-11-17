package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.ClassTreeBuilder;

import java.util.Collection;
import java.util.List;

// todo: remove TreeBuild from this class type definition
public class Variable extends TreeBuilder implements Valuable {
    String name;
    ClassTreeBuilder type;
    Token polymorphicIdentifier;

    public Variable(String name, ClassTreeBuilder type, TreeBuilder parent) {
        super(parent);
        this.name = name;
        this.type = type;
        this.polymorphicIdentifier = null;
    }

    public Variable(String name, ClassTreeBuilder type, Token polymorphicIdentifier) {
        // am I right???
        super(type);

        this.name = name;
        this.type = type;
        this.polymorphicIdentifier = polymorphicIdentifier;
    }

    public ClassTreeBuilder getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    @Override
    public Variable getVariable() {
        return this;
    }

    @Override
    public void build() {
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return to;
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return DeferredVisitorAction.empty;
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return List.of();
    }
}
