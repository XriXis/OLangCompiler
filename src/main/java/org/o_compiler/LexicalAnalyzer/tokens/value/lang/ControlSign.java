package org.o_compiler.LexicalAnalyzer.tokens.value.lang;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

public enum ControlSign implements TokenValue, TokenDescription {
    ASSIGN(":=", ":="),
    LAMBDA("=>", "=>"),
    DYNAMIC_DISPATCH(".", "\\."),
    PARENTHESIS_OPEN("(", "\\("),
    PARENTHESIS_CLOSED(")", "\\)"),
    BRACKET_OPEN("[", "\\["),
    BRACKET_CLOSED("]", "\\]"),
    END_LINE("\\n", "\n"),
    CR("\\r", "\r"),
    SEPARATOR(",", ","),
    SPACE(" ", " *"),
    TABULATION("\\t", "\t"),
    COLUMN(":", ":"),
    LINE_DELIMITER("\\", "\\\\"),
    COMMENT("//...", "//["
            + (char) 0 +
            '-' +
            (char) 9 +
            (char) 11 +
            '-' +
            Character.MAX_VALUE
            + "]*\n"),
    MULTILINE_COMMENT("/*...*/", "/\\*.*\\*/"),
    ;

    final String literal;
    final String pattern;
    ControlSign(String token, String pattern){
        literal = token;
        this.pattern = pattern;
    }

    @Override
    public String value() {
        return literal;
    }

    @Override
    public String pattern() {
        return pattern;
    }

    @Override
    public int priority(){ return 2; }

    @Override
    public TokenValue corresponding(String cumulated) {
        return this;
    }
}
