package org.o_compiler.LexicalAnalyzer.tokens.value.lang;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

public enum Keyword implements TokenValue, TokenDescription {
    CLASS,
    METHOD,
    IS,
    END,
    EXTENDS,
    RETURN,
    THIS,
    THEN,
    WHILE,
    LOOP,
    VAR,
    ;


    @Override
    public String value() {
        return this.name().toLowerCase();
    }

    @Override
    public String pattern() {
        return value();
    }

    @Override
    public int priority(){ return 0; }

    @Override
    public TokenValue corresponding(String cumulated) {
        return this;
    }
}
