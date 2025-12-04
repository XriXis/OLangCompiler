package org.o_compiler.Semantic.ClassRelations;

import org.o_compiler.Semantic.SemanticTests;

import java.nio.file.Path;

public abstract class ClassRelationsTests extends SemanticTests {
    protected Path getDirectory(){
        return super.getDirectory().resolve("classRelations");
    }
}
