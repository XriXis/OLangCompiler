package org.o_compiler.LexicalAnalyzer;

import org.o_compiler.LexicalAnalyzer.parser.TokenStream;

import java.io.InputStream;

public class Analyzer {
    TokenStream tokens;

    public Analyzer(InputStream target){
        tokens = new TokenStream(target);
        for (var item: tokens){
            //System.out.println(item.getClass().getCanonicalName());
        }
    }
}
