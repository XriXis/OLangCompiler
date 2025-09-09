package org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;

public class IdentifierDescription implements TokenDescription {
    public String pattern(){
        return "[A-Za-z_][A-Za-z0-9_]*";
    }
}
