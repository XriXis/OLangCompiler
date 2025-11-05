package org.o_compiler.Semantic.Scopes;

import org.junit.jupiter.api.Test;

public class SemanticScopesErrorTests extends SemanticScopesTests {
    @Test
    public void doubleDeclarationShouldNotCompile() {
        assert !testFile("DoubleDeclarationSameScope.zu") : "DoubleDeclarationSameScope.zu should not compile";
    }

    @Test
    public void usageOfUndeclaredVariableShouldNotCompile() {
        assert !testFile("UsageOfUndeclaredVariable.zu") : "UsageOfUndeclaredVariable.zu should not compile";
    }
}
