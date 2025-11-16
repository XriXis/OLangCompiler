package org.o_compiler.SyntaxAnalyzer.builder.PredefinedClasses;

import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.StreamSupport;

public class PredefinedClassesParser {
    private static final Path sourceCodeDir = Path.of("src/main/resources/PredefinedClasses/SourceCode");

    public static Iterator<Token> getPredefinedClassesStream() {
        return uniteFiles();
    }

    private static Iterator<Token> uniteFiles() {
        try {
            return Files.list(PredefinedClassesParser.sourceCodeDir)
                    .flatMap(path -> {
                        try {
                            Iterator<Token> tokenIterator = new TokenStream(Files.newInputStream(path));
                            Iterable<Token> iterable = new IteratorSingleIterableAdapter<>(tokenIterator);
                            return StreamSupport.stream(iterable.spliterator(), false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(token -> !token.isWhitespace())
                    .iterator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Iterator<Token> tokens = uniteFiles();
        while (tokens.hasNext()) {
            Token token = tokens.next();
            System.out.println(token);
        }
    }
}


// todo: it is not actual todo, but it seems strange, that overloads are not implemented (not supposed), but you use
//  them in the source code (@Integer::LessThan(Real)  @Integer::LessThan(Integer) and many others)