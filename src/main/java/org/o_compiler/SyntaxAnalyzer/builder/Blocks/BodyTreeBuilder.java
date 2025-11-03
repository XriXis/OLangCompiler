package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

public class BodyTreeBuilder extends BlockBuilder {
    public BodyTreeBuilder(Iterable<Token> source, MethodTreeBuilder parent) {
        super(source, parent);
    }

    @Override
    public void build() {
        super.build();
        var p = (MethodTreeBuilder) parent;
        if (p.isTypeOf(getClass("Void")))
            return;
        // todo: get rid of way it is done
        if (p.getParent().isPredefined()) return;
        if (p.isConstructor()) return;
        if (!validate())
            throw new CompilerError("Not all flows lead to return value in method " + parent);
    }
}
