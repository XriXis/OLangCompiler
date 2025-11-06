package org.o_compiler.Semantic.Calls;

import org.junit.jupiter.api.Test;

public class SemanticCallsErrorTests extends SemanticCallsTests {
    @Test
    public void improperTypesCallShouldNotCompile(){
        assert !testFile("ImproperTypesCall.zu") : "ImproperTypesCall.zu should not compile";
    }

    @Test
    public void overkilledCallShouldNotCompile(){
        assert !testFile("OverkilledCall.zu") : "OverkilledCall.zu should not compile";
    }

    @Test
    public void unsufficientCallShouldNotCompile(){
        assert !testFile("UnsufficientCall.zu") : "UnsufficientCall.zu should not compile";
    }
}
