package org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;

public class IdentifierDescription implements TokenDescription {
    public String pattern(){
        return "[A-z_][A-z_0-9]*";
    }
}
