package org.o_compiler.LexicalAnalyzer.parser;

import java.io.IOException;
import java.io.InputStream;

public class CharStream {
    static final char NO_CHAR = (char) -1;
    final InputStream stream;
    char last = NO_CHAR;

    public CharStream(final InputStream stream) {
        this.stream = stream;
    }

    public char peek() throws IOException {
        if (last == NO_CHAR)
            last = (char) stream.read();
        return last;
    }

    public char pop() throws IOException {
        char res = last;
        last = NO_CHAR;
        peek();
        return res;
    }
}
