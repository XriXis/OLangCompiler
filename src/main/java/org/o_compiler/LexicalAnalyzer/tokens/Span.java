package org.o_compiler.LexicalAnalyzer.tokens;

public class Span {
    final int line;
    final int pos;

    public Span(int row, int col){
        line = row;
        pos = col;
    }
}
