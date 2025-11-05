package org.o_compiler.Semantic.Generics;

import org.o_compiler.Semantic.SemanticTests;

import java.nio.file.Path;

public abstract class SemanticGenericTests extends SemanticTests {
    protected Path getDirectory(){
        return super.getDirectory().resolve("generics");
    }
}
