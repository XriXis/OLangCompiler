package org.o_compiler.LexicalAnalyzer.parser;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.io.InputStream;
import java.util.Iterator;

public class TokenStream implements Iterator<Token> {
    CharStream source;

    public TokenStream(InputStream target) {
        source = new CharStream(target);
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Token next() {

        return null;
    }
}
