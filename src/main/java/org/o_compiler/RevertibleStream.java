package org.o_compiler;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class RevertibleStream<T> implements Iterator<T> {
    private final Iterator<T> source;
    private final SizedStack<T> history;
    private final Stack<T> buffer;

    public RevertibleStream(Iterator<T> source, int historySize) {
        this.source = source;
        this.history = new SizedStack<>(historySize);
        this.buffer = new Stack<>();
    }

    public T pop() {
        if (!buffer.isEmpty())
            history.push(buffer.pop());
        else if (!source.hasNext())
            throw new NoSuchElementException("End of stream");
        else
            history.push(source.next());
        return history.peek();
    }

    public void revert() {
        if (history.isEmpty()) {
            throw new RuntimeException("Reverting to unread stream");
        }
        buffer.push(history.pop());
    }

    public void imitateNext(T val){
        buffer.push(val);
    }

    public T lastRead(){
        return history.peek();
    }

    @Override
    public boolean hasNext() {
        return !buffer.isEmpty() || source.hasNext();
    }

    @Override
    public T next() {
        return pop();
    }
}
