package org.o_compiler.Semantic.Return;

import org.junit.jupiter.api.Test;

public class SemanticReturnErrorTests extends SemanticReturnTests {
    @Test
    public void incompleteReturnShouldNotCompile() {
        assert !testFile("IncompleteReturn.zu") : "IncompleteReturn.zu should not compile";
    }

    @Test
    public void noReturnShouldNotCompile() {
        assert !testFile("NoReturn.zu") : "NoReturn should not compile";
    }

    @Test
    public void returnInIncompleteBranchShouldNotCompile() {
        assert !testFile("ReturnInIncompleteBranch.zu") : "ReturnInIncompleteBranch.zu should not compile";
    }

    @Test
    public void nonEmptyReturnShouldNotCompile() {
        assert !testFile("NonEmptyReturnInConstructor.zu") : "ReturnInIncompleteBranch.zu should not compile";
    }
}
