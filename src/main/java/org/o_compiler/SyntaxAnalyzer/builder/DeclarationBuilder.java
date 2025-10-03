package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.util.ArrayList;
import java.util.Iterator;

public class DeclarationBuilder implements BuildTree {
    Iterator<Token> source;
    ArrayList<Token> children;

    // source - should be already a line
    public DeclarationBuilder(Iterable<Token> source) {
        // [var], [x], [:], [Int]
        this.source = source.iterator();
    }

    @Override
    public void build() {
//

    }

    @Override
    public boolean encloseName(String name) {
        return false;
    }

    @Override
    public BuildTree getParent() {
        return null;
    }
}
