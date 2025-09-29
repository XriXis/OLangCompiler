package org.o_compiler;

import org.junit.jupiter.api.Test;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;

import java.nio.file.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class LexicalAnalyzerTest {

    private final Path directory = Paths.get("src/test/resources/tests/lexicographical");

    @Test
    public void testCorrectFiles() throws IOException {
        Files.list(directory)
                .filter(Files::isRegularFile)
                .filter(file -> !file.getFileName().toString().contains("not_correct"))
                .forEach(file -> {
                    try (InputStream inputStream = Files.newInputStream(file)) {
                        System.out.println("Analyzing file: " + file.getFileName());
                        assertTrue(analyzeLexicographically(inputStream), "File " + file.getFileName() + " should be correctly lexically structured.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void testNotCorrectFiles() throws IOException {
        Files.list(directory)
                .filter(Files::isRegularFile)
                .filter(file -> file.getFileName().toString().contains("not_correct"))
                .forEach(file -> {
                    try (InputStream inputStream = Files.newInputStream(file)) {
                        System.out.println("Analyzing file: " + file.getFileName());
                        assertFalse(analyzeLexicographically(inputStream), "File " + file.getFileName() + " should be correctly lexically structured.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public boolean analyzeLexicographically(InputStream inputStream) {
        try {
            for (var token : new IteratorSingleIterableAdapter<>(new TokenStream(inputStream))) {
                System.out.print(token + " ");
            }
        } catch (Exception e) {
            System.out.println();
            return false;
        }
        System.out.println();
        return true;
    }
}
