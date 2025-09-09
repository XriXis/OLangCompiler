package org.o_compiler.LexicalAnalyzer.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class CharStream implements Iterator<Character>, Iterable<Character>{
    static final char NO_CHAR = (char) -1;
    final InputStream stream;
    char last = NO_CHAR;

    public CharStream(final InputStream stream) throws IOException {
        this.stream = stream;
        pop();
    }

    public char peek() {
        return last;
    }

    public char pop() throws IOException {
        char res = last;
//        if (last=='\r') System.out.print("\\r");
        last = (char) stream.read();
        return res;
    }

    @Override
    public boolean hasNext() {
        return last != NO_CHAR;
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
