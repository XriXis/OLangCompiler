package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.ClassTreeBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BodyTreeBuilder extends BlockBuilder {
    public BodyTreeBuilder(Iterable<Token> source, ClassTreeBuilder parent) {
        super(source, parent);
        this.parent = parent;
    }

    @Override
    public void build() {

    }
}
