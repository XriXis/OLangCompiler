package org.o_compiler.Semantic.ClassRelations;

import org.junit.jupiter.api.Test;

public class ClassRelationsErrorTests extends ClassRelationsTests {
    @Test
    public void crossInheritanceShouldNotCompile(){
        assert !testFile("CrossInheritance.zu") : "ImproperTypesCall.zu should not compile";
    }

    @Test
    public void circularInheritanceShouldNotCompile(){
        assert !testFile("CircularInheritance.zu") : "OverkilledCall.zu should not compile";
    }
}
