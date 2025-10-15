package org.o_compiler.LexicalAnalyzer.tokens.value.client.literal;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

import java.util.function.Function;

public enum LiteralType implements TokenDescription {
    BOOLEAN("true|false", Boolean::parseBoolean, "Boolean"),
    INTEGER("[0-9]+", Integer::parseInt, "Integer"),
    REAL("[0-9]*\\.[0-9]+|[0-9]+\\.[0-9]*", Double::parseDouble, "Real"),
    STRING("\"["
            + (char) 0 +
            '-' +
            (char) ('\"' - 1) +
            (char) ('\"' + 1) +
            '-' +
            Character.MAX_VALUE
            + "]*\"", str -> str, "String");

    final String pattern;
    public final Function<String, ?> parser;
    final String OLangTypeName;

    LiteralType(String pattern, Function<String, ?> parser, String cls) {
        this.pattern = pattern;
        this.parser = parser;
        this.OLangTypeName = cls;
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
        res.oLangClassName = OLangTypeName;
        return res;
    }
}
