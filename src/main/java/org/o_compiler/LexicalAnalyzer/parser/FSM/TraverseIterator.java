package org.o_compiler.LexicalAnalyzer.parser.FSM;

public interface TraverseIterator<T> {
    void feed(char ch);
    boolean isOnlyGarbage();
    T result();
    String pathTaken();
}
