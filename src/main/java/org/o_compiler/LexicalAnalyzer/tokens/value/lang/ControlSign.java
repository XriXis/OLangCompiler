package org.o_compiler.LexicalAnalyzer.tokens.value.lang;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            (char) ('\n' - 1) +
            (char) ('\n' + 1) +
            '-' +
            Character.MAX_VALUE
            + "]*\n"),
//    MULTILINE_COMMENT("/*...*/", "/\\*.*\\*/"),
    ;

    final String literal;
    final String pattern;
    final static Set<ControlSign> whitespaces = new HashSet<>();

    static {
        whitespaces.addAll(List.of(new ControlSign[]
                {CR, SPACE, TABULATION, COMMENT}
        ));
    }

    public boolean isWhitespace(){
        return whitespaces.contains(this);
    }

    ControlSign(String token, String pattern) {
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
    public int priority() {
        return 2;
    }

    @Override
    public TokenValue corresponding(String cumulated) {
        return this;
    }
}
