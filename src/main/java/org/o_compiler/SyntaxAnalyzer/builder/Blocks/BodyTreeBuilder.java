package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

public class BodyTreeBuilder extends BlockBuilder {
    public BodyTreeBuilder(Iterable<Token> source, MethodTreeBuilder parent) {
        super(source, parent);
    }
}
