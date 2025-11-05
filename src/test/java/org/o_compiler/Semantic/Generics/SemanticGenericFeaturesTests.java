package org.o_compiler.Semantic.Generics;

import org.junit.jupiter.api.Test;

public class SemanticGenericFeaturesTests extends SemanticGenericTests {
    @Test
    public void genericInheritancePossibilityShouldCompile() {
        assert testFile("GenericInheritancePossibility.zu") : "GenericInheritancePossibility.zu should compile successfully";
    }

    @Test
    public void initializationOfTheGenericClassShouldCompile() {
        assert testFile("InitializationOfTheGenericClass.zu") : "InitializationOfTheGenericClass.zu should compile successfully";
    }

    @Test
    public void properArgumentRecognitionShouldCompile() {
        assert testFile("ProperArgumentRecognition.zu") : "ProperArgumentRecognition.zu should compile successfully";
    }

    @Test
    public void specificationFeatureShouldCompile() {
        assert testFile("SpecificationFeature.zu") : "SpecificationFeature.zu should compile successfully";
    }

    @Test
    public void usageOfTheGenericMethodResultShouldCompile() {
        assert testFile("UsageOfTheGenericMethodResult.zu") : "UsageOfTheGenericMethodResult.zu should compile successfully";
    }

    @Test
    public void buildInGenericUsageShouldCompile() {
        assert testFile("BuildInGenericUsage.zu") : "BuildInGenericUsage.zu should compile successfully";
    }
}
