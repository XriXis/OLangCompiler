package org.o_compiler.CodeGeneration;

public interface DeferredVisitorAction {
    DeferredVisitorAction empty = () -> {
    };

    void act();
}
