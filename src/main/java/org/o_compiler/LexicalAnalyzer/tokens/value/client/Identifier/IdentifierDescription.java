package org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

public class IdentifierDescription implements TokenDescription {
    @Override
    public String pattern(){
        return "[A-Za-z_][A-Za-z0-9_]*";
    }
    @Override
    public int priority() { return 10; }

    @Override
    public TokenValue corresponding(String cumulated) {
        return new Identifier(cumulated);
    }
}
