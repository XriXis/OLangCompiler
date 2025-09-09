package org.o_compiler.LexicalAnalyzer.tokens.value;

public interface TokenDescription {
    String pattern();
    int priority();
    TokenValue corresponding(String cumulated);
}
