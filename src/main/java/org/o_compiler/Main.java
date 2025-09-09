package org.o_compiler;

import org.o_compiler.LexicalAnalyzer.parser.SizedStack;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try (InputStream target = new FileInputStream(args[0])){
            ArrayList<Token> res = new ArrayList<>();
            var stream = new TokenStream(target);

            for (var token: stream){
                res.add(token);
                System.out.println(token);
            }

//            System.out.println(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}