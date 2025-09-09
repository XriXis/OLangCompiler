package org.o_compiler.LexicalAnalyzer.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Stack;

public class CharStream implements Iterator<Character>, Iterable<Character> {
    static final char NO_CHAR = (char) -1;
    final InputStream stream;
    SizedStack<Character> history;
    Stack<Character> buffer;

    public CharStream(final InputStream stream) throws IOException {
        this.stream = stream;
        history = new SizedStack<>(5);
        buffer = new Stack<>();
        pop();
        buffer.push(history.peek());
    }

    public char pop() throws IOException {
        if (!buffer.isEmpty()) {
            return buffer.pop();
        }
        history.push((char) stream.read());
        return history.peek();
    }

    public void revert() {
        if (history.size() == 0) throw new RuntimeException("Reverting to unread stream");
        buffer.push(history.pop());
    }

    @Override
    public boolean hasNext() {
        return ((!buffer.isEmpty()) || (history.peek() != NO_CHAR));
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
