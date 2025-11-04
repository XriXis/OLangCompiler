package org.o_compiler;

import org.junit.jupiter.api.Test;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.RootTreeBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NamedSemanticTests {
    private final Path directory = Paths.get("src","test", "resources", "tests", "syntax", "named").toAbsolutePath();

    public boolean testFile(String fileName){
        var file = directory.resolve(fileName);
        try (InputStream inputStream = new FileInputStream(file.toFile())) {
            return runFile(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CompilerError ignored){
            return false;
        }
    }


    public boolean runFile(InputStream inputStream) {
        try {
            RootTreeBuilder rootTreeBuilder = new RootTreeBuilder(
                    new IteratorSingleIterableAdapter<>(new TokenStream(inputStream)));
            rootTreeBuilder.build();
            System.out.println(rootTreeBuilder.viewWithoutPredefined());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


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
