package org.o_compiler.Return;

import org.junit.jupiter.api.Test;

public class NamedSemanticReturnFeaturesTests extends NamedSemanticReturnTests {
    @Test
    public void codeAfterReturnShouldCompile() {
        assert testFile("CodeAfterReturn.zu") : "CodeAfterReturn.zu should compile successfully";
    }

    @Test
    public void properBranchedReturnShouldCompile() {
        assert testFile("ProperBranchedReturn.zu") : "ProperBranchedReturn.zu should compile successfully";
    }

    @Test
    public void properReturnShouldCompile() {
        assert testFile("ProperReturn.zu") : "ProperReturn.zu should compile successfully";
    }

    @Test
    public void emptyReturnShouldCompile() {
        assert testFile("EmptyReturnInConstructor.zu") : "ReturnInIncompleteBranch.zu should compile";
    }

    @Test
    public void noReturnInVoidFunctionShouldCompile() {
        assert testFile("NoReturnInVoidFunction.zu") : "NoReturnInVoidFunction.zu should compile";
    }
}
