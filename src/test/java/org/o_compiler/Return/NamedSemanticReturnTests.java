package org.o_compiler.Return;

import org.o_compiler.NamedSemanticTests;

import java.nio.file.Path;

public abstract class NamedSemanticReturnTests extends NamedSemanticTests {
    protected Path getDirectory(){
        return super.getDirectory().resolve("return");
    }
}
