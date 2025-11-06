package org.o_compiler.Semantic.Calls;

import org.o_compiler.Semantic.SemanticTests;

import java.nio.file.Path;

public abstract class SemanticCallsTests extends SemanticTests {
    protected Path getDirectory(){
        return super.getDirectory().resolve("calls");
    }
}
