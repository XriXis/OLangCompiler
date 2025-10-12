package org.o_compiler.LexicalAnalyzer.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

class InputIterator implements Iterator<Character> {
    private static final int EOF = -1;
    private final InputStream stream;
    private int nextChar;

    /**
     * Object, that turn interface of InputStream to Iterator<Character>
     * @param stream opened input stream
     * @throws RuntimeException(IOException) when read from stream is impossible
     */
    public InputIterator(InputStream stream) {
        this.stream = stream;
        try {
            this.nextChar = stream.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return nextChar != EOF;
    }

    @Override
    public Character next() {
        int current = nextChar;
        try {
            nextChar = stream.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (char) current;
    }
}
