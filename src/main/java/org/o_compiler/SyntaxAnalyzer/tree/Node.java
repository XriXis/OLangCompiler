package org.o_compiler.SyntaxAnalyzer.tree;

import java.util.Objects;

// class Node
public class Node<T> {
    T value;
    Node<T> left, right;

    Node(T value) {
        this.value = Objects.requireNonNull(value);
        left = right = null;
    }
}