package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;

import java.util.ArrayList;
import java.util.Iterator;

public class DeclarationBuilder implements BuildTree {
    Iterator<Token> source;
    ArrayList<Token> children;

    // source - should be already a line
    protected DeclarationBuilder(Iterable<Token> source) {
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
}
