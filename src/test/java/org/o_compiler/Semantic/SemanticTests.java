package org.o_compiler.Semantic;

import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.RootTreeBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class SemanticTests {
    private final Path directory = Paths.get("src","test", "resources", "tests", "syntax", "named").toAbsolutePath();

    protected Path getDirectory(){
        return directory;
    }

    public boolean testFile(String fileName){
        var file = getDirectory().resolve(fileName);
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
}
