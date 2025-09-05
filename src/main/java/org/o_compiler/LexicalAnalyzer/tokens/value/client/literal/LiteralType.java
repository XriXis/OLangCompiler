package org.o_compiler.LexicalAnalyzer.tokens.value.client.literal;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;

import java.util.function.Function;

public enum LiteralType implements TokenDescription {
    BOOLEAN("true|false", Boolean::parseBoolean),
    INTEGER("[0-9]+", Integer::parseInt),
    REAL("[0-9]*\\.[0-9]+|[0-9]+\\.[0-9]*", Double::parseDouble),
    STRING("\".*\"", str->str)
    ;

    final String pattern;
    public final Function<String, ?> parser;

    LiteralType(String pattern, Function<String, ?> parser){
        this.pattern = pattern;
        this.parser = parser;
    }

    @Override
    public String pattern() {
        return pattern;
    }
}
