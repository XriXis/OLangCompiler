package org.o_compiler;

import java.util.ArrayDeque;

public class SizedStack<T> {
    private final ArrayDeque<T> entry;
    private final int cap;

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

    public boolean isEmpty() {
        return entry.isEmpty();
    }
}
