package org.o_compiler.CodeGeneration;

public interface DeferredVisitorAction {
    DeferredVisitorAction empty = (ignored) -> {
    };

    void act(BuildTreeVisitor v);
}
