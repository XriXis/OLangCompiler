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
                /*Arrays.stream(Keyword.values())
                .map(obj -> Pattern.quote(obj.value()))
                .collect(Collectors.joining("|"));*/
    }
}
