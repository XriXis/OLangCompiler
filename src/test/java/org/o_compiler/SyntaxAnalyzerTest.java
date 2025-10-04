package org.o_compiler;

import org.junit.jupiter.api.Test;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.RootTreeBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SyntaxAnalyzerTest {

    private final Path directory = Paths.get("src/test/resources/tests/syntax");
    private final Path correctFilesDir = directory.resolve("correct");
    private final Path notCorrectFilesDir = directory.resolve("not-correct");

    @Test
    public void testCorrectFiles() throws IOException {
        Files.list(correctFilesDir)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try (InputStream inputStream = Files.newInputStream(file)) {
                        System.out.println("Analyzing file: " + file.getFileName());
                        assertTrue(analyzeCorrectSyntax(inputStream), "File " + file.getFileName() + " should correct syntax.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void testNotCorrectFiles() throws IOException {
        Files.list(notCorrectFilesDir)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try (InputStream inputStream = Files.newInputStream(file)) {
                        System.out.println("Analyzing file: " + file.getFileName());
                        assertFalse(analyzeNotCorrectSyntax(inputStream), "File " + file.getFileName() + " has not found mistake.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public boolean analyzeCorrectSyntax(InputStream inputStream) {
        try {
//            for (var token: new IteratorSingleIterableAdapter<>(new TokenStream(inputStream))) {
//                System.out.println(token);
//            }
            RootTreeBuilder rootTreeBuilder = new RootTreeBuilder(
                    new IteratorSingleIterableAdapter<>(new TokenStream(inputStream)));

            rootTreeBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean analyzeNotCorrectSyntax(InputStream inputStream) {
        try {
            RootTreeBuilder rootTreeBuilder = new RootTreeBuilder(
                    new IteratorSingleIterableAdapter<>(new TokenStream(inputStream)));

            rootTreeBuilder.build();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
