package org.o_compiler.LexicalAnalyzer.tokens.value.lang;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

public enum ControlSign implements TokenValue, TokenDescription {
    ASSIGN(":="),
    LAMBDA("=>"),
    DYNAMIC_DISPATCH("."),
    PARENTHESIS_OPEN("("),
    PARENTHESIS_CLOSED(")"),
    BRACKET_OPEN("["),
    BRACKET_CLOSED("]"),
    END_LINE("\n"),
    SEPARATOR(","),
    SPACE(" "),
    TABULATION("\t"),
    COLUMN(":")
    ;

    final String literal;
    ControlSign(String token){
        literal = token;
    }

    @Override
    public String value() {
        return literal;
    }

    public String pattern() {
        return value();
                /*Arrays.stream(Keyword.values())
                .map(obj -> Pattern.quote(obj.value()))
                .collect(Collectors.joining("|"));*/
    }
}
