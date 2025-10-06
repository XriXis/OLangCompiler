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
            var stream = new IteratorSingleIterableAdapter<>(new TokenStream(target));
            ArrayList<Pair<Token, String>> toPrint = new ArrayList<>();
            for (var token : stream) {
                toPrint.add(new Pair<>(token, " at " + token.position()));
            }
            printAsTable(toPrint);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static<T1, T2> void printAsTable(Iterable<Pair<T1, T2>> coll){
        int maxLen = 0;
        ArrayList<Pair<String, String>> mapped = new ArrayList<>();
        for (var p: coll){
            mapped.add(new Pair<>(p.o1.toString(), p.o2.toString()));
            maxLen = Math.max(maxLen, mapped.getLast().o1.length());
        }
        for (var p: mapped){
            String tabs = '\t' + "\t".repeat(Math.max(0, (maxLen - p.o1.length()) / 4));
            System.out.println(p.o1 + tabs + p.o2);
        }
    }
}