package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

import java.util.ArrayList;
import java.util.Iterator;

public class WhileTreeBuilder extends BlockBuilder {
    public WhileTreeBuilder(Iterable<Token> source, BuildTree parent) {
        super(source, parent);
    }

    @Override
    public void build() {

    }
}
