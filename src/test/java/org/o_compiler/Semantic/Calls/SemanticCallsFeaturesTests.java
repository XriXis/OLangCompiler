package org.o_compiler.Semantic.Calls;

import org.junit.jupiter.api.Test;

public class SemanticCallsFeaturesTests extends SemanticCallsTests {
    @Test
    public void regularCallShouldCompile(){
        assert testFile("RegularCall.zu"): "RegularCall.zu should compile";
    }

    @Test
    public void subtypePassedShouldCompile(){
        assert testFile("SubtypePassed.zu"): "SubtypePassed.zu should compile";
    }
}
