package org.o_compiler.LexicalAnalyzer.tokens;

public class Span {
    final int line;
    final int pos;

    public Span(int row, int col) {
        line = row;
        pos = col;
    }

    // in case of performance issues could be changed to mutate the object
    public Span feed(char ch){
        if (ch=='\n')
            return new Span(line+1, 0);
        if (ch=='\t')
            return new Span(line, pos+4);
        return new Span(line, pos+1);
    }

    @Override
    public String toString() {
        return "line " + line + ", col " + pos + " (" + line + ":" + pos + ")";
    }
}
