package org.o_compiler;

import java.util.Iterator;

public class IteratorSingleIterableAdapter<T> implements Iterable<T> {
    Iterator<T> wrapped;

    public IteratorSingleIterableAdapter(Iterator<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Iterator<T> iterator() {
        return wrapped;
    }
}
