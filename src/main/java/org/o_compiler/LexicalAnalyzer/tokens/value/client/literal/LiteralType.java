package org.o_compiler.LexicalAnalyzer.tokens.value.client.literal;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

import java.util.function.Function;

public enum LiteralType implements TokenDescription {
    BOOLEAN("true|false", Boolean::parseBoolean),
    INTEGER("[0-9]+", Integer::parseInt),
    REAL("[0-9]*\\.[0-9]+|[0-9]+\\.[0-9]*", Double::parseDouble),
    STRING("\"["
            + (char) 0 +
            '-' +
            (char) ('\"' - 1) +
            (char) ('\"' + 1) +
            '-' +
            Character.MAX_VALUE
            + "]*\"", str -> str);

    final String pattern;
    public final Function<String, ?> parser;

    LiteralType(String pattern, Function<String, ?> parser) {
        this.pattern = pattern;
        this.parser = parser;
    }

    @Override
    public String pattern() {
        return pattern;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public TokenValue corresponding(String cumulated) {
        var res = new Literal<>(cumulated);
        res.type = this;
        return res;
    }
}
