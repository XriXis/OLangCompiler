package org.o_compiler.SyntaxAnalyzer.tree;


public class BinaryTree<T extends Comparable<T>> {
    Node<T> root;


    // insert
    public void insert(T value) {
        root = insertRec(root, value);
    }

    // insert recursively
    private Node<T> insertRec(Node<T> root, T value) {
        if (root == null) return new Node<>(value);
        if (value.compareTo(root.value) < 0) root.left = insertRec(root.left, value);
        else root.right = insertRec(root.right, value);
        return root;
    }

    // insert to the given node
    public void insert(Node<T> node, T value) {
        if (value.compareTo(node.value) < 0) node.left = insertRec(node.left, value);
        else node.right = insertRec(node.right, value);
    }

    // search
    public boolean search(T value) {
        return searchRec(root, value);
    }

    // search recursively
    private boolean searchRec(Node<T> root, T value) {
        if (root == null) return false;
        if (root.value.equals(value)) return true;
        return (value.compareTo(root.value)) < 0 ? searchRec(root.left, value) : searchRec(root.right, value);
    }
}