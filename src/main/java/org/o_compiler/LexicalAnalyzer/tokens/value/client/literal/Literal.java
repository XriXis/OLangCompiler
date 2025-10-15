package org.o_compiler.LexicalAnalyzer.tokens.value.client.literal;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;


public class Literal<T> implements TokenValue {
    String value;
    LiteralType type;
    // todo: improve!
    String oLangClassName;

    public Literal(String captured) {
        value = captured;
    }

    public T bytes() {
        return (T) type.parser.apply(value);
    }

    @Override
    public String value() {
        return value;
    }

    public String getOLangClassName(){
        return oLangClassName;
    }
}