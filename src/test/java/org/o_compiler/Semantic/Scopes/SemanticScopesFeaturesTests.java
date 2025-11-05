package org.o_compiler.Semantic.Scopes;

import org.junit.jupiter.api.Test;

public class SemanticScopesFeaturesTests extends SemanticScopesTests {
    @Test
    public void argumentsShadowingShouldCompile() {
        assert testFile("ArgumentsShadowing.zu") : "ArgumentsShadowing.zu should compile successfully";
    }

    @Test
    public void differentEntitiesWithSameNamesShouldCompile() {
        assert testFile("DifferentEntitiesWithSameNames.zu") : "DifferentEntitiesWithSameNames.zu should compile successfully";
    }

    @Test
    public void shadowingShouldCompile() {
        assert testFile("Shadowing.zu") : "Shadowing.zu should compile successfully";
    }
}
