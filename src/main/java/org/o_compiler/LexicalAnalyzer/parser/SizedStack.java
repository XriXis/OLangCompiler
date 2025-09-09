package org.o_compiler.LexicalAnalyzer.parser;

import java.util.ArrayDeque;

public class SizedStack<T> {
    ArrayDeque<T> entry;
    int cap;

    public SizedStack(int capacity){
        cap = capacity;
        entry = new ArrayDeque<>();
    }

    public void push(T item){
        entry.push(item);
        if (entry.size()>cap){
            entry.removeLast();
        }
    }

    public T pop(){
        return entry.pop();
    }

    public T peek(){
        return entry.peek();
    }

    public int size(){
        return entry.size();
    }
}
