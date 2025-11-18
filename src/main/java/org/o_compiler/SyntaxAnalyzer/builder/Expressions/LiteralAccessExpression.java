package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

import java.util.Collection;
import java.util.List;

public class LiteralAccessExpression<T> extends ExpressionTreeBuilder{
    Literal<T> value;
    public LiteralAccessExpression(Literal<T> value, TreeBuilder parent) {
        super(parent);
        this.value = value;
        this.type = getClass(value.getOLangClassName());
        // todo: conversion of java type of T to proper type of o_lang
        //  this.type = RootTreeBuilder.translateType(T) // improper syntax, but smth similar is expected
    }

    @Override
    public void build() {

    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, "Usage of the literal value: " + value.value());
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitLiteralAccess(this, type, value);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return List.of();
    }
}
