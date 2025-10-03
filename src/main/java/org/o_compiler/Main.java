package org.o_compiler;

import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try (InputStream target = Files.newInputStream(Path.of(args[0]))) {
            ArrayList<Token> res = new ArrayList<>();
            var stream = new IteratorSingleIterableAdapter<>(new TokenStream(target));
            for (var token : stream) {
                res.add(token);
                System.out.println(token);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}