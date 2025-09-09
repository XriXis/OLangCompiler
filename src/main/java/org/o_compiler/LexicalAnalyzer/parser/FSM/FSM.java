package org.o_compiler.LexicalAnalyzer.parser.FSM;

public interface FSM<T> {
    TraverseIterator<T> traverse();
}
