package org.o_compiler.LexicalAnalyzer.tokens;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;

import javax.swing.tree.TreePath;
import javax.xml.stream.FactoryConfigurationError;

public record Token(TokenValue entry, Span position) {
    public Token(TokenValue entry, int line, int pos) {
        this(entry, new Span(line, pos - entry.value().length() + 1));
    }

    // todo: get rid of explicit type usage
    public boolean isWhitespace() {
        if (!(entry instanceof ControlSign)) return false;
        return ((ControlSign) entry).isWhitespace();
    }

    @Override
    public String toString() {
        return "[" + entry.getClass().getSimpleName() + ": <" + entry.value() + ">]";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Token)) {
            return false;
        } else return entry.equals(((Token) object).entry);
    }
}
