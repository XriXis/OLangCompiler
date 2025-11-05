package org.o_compiler.Semantic.Return;

import org.o_compiler.Semantic.SemanticTests;

import java.nio.file.Path;

public abstract class SemanticReturnTests extends SemanticTests {
    protected Path getDirectory(){
        return super.getDirectory().resolve("return");
    }
}
