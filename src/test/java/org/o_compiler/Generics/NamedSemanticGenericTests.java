package org.o_compiler.Generics;

import org.o_compiler.NamedSemanticTests;

import java.nio.file.Path;

public abstract class NamedSemanticGenericTests extends NamedSemanticTests {
    protected Path getDirectory(){
        return super.getDirectory().resolve("generics");
    }
}
