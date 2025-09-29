package org.o_compiler.SyntaxAnalyzer.tree;

import org.o_compiler.SyntaxAnalyzer.SyntaxNode;

import java.util.Objects;

// class Node
public class Node<T> implements SyntaxNode {
    T value;
    Node<T> left, right;

    Node(T value) {
        this.value = Objects.requireNonNull(value);
        left = right = null;
    }
}