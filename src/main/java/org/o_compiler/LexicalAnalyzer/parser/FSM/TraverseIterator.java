package org.o_compiler.LexicalAnalyzer.parser.FSM;

public interface TraverseIterator<T> {
    void feed(char ch);
    boolean isEnd();
    T result();
    String pathTaken();
}
