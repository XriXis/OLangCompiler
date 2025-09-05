package org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

public class Identifier implements TokenValue {
    String value;

    public Identifier(String captured){
        value = captured;
    }

    @Override
    public String value() {
        return value;
    }
}
