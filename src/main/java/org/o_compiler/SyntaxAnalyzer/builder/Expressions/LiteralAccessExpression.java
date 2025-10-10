package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

import java.util.List;

public class LiteralAccessExpression<T> extends ExpressionTreeBuilder{
    Literal<T> value;
    public LiteralAccessExpression(Literal<T> value, BuildTree parent) {
        this.value = value;
        this.parent = parent;
        // todo: conversion of java type of T to proper type of o_lang
        // this.type = RootTreeBuilder.translateType(T) // improper syntax, but smth similar is expected
    }

    @Override
    public void build() {

    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, "Usage of the literal value: " + value.value(), List.of());
    }
}
