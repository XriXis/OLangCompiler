package org.o_compiler.LexicalAnalyzer.tokens;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

public class Token {
    TokenValue entry;
    Span position;

    public Token(TokenValue entry, int line, int pos){
        this.entry = entry;
        position = new Span(line, pos);
    }

    @Override
    public String toString(){
        return "["+entry.getClass().getSimpleName()+": <"+entry.value()+">]";
    }
}
