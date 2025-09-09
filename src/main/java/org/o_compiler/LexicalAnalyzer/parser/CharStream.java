package org.o_compiler.LexicalAnalyzer.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Stack;

public class CharStream implements Iterator<Character>, Iterable<Character> {
    static final int EOF = -1;
    final InputStream stream;
    SizedStack<Integer> history;
    Stack<Integer> buffer;

    public CharStream(final InputStream stream) throws IOException {
        this.stream = stream;
        history = new SizedStack<>(5);
        buffer = new Stack<>();
        pop();
        buffer.push(history.peek());
    }

    public char pop() throws IOException {
        if (!buffer.isEmpty()) {
            return (char) buffer.pop().intValue();
        }
        int val = stream.read();
        history.push(val);
        return (char) history.peek().intValue();
    }

    public void revert() {
        if (history.size() == 0) throw new RuntimeException("Reverting to unread stream");
        buffer.push(history.pop());
    }

    @Override
    public boolean hasNext() {
        return !(buffer.isEmpty() && history.peek() == EOF || !buffer.isEmpty() && buffer.peek() == EOF);
    }

    @Override
    public Character next() {
        try {
            return pop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<Character> iterator() {
        return this;
    }
}
