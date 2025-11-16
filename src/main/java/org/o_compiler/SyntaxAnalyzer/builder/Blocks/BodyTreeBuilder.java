package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.ClassTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.MethodTreeBuilder;

public class BodyTreeBuilder extends BlockBuilder {
    public BodyTreeBuilder(Iterable<Token> source, MethodTreeBuilder parent) {
        super(source, parent);
    }

    @Override
    public void build() {
        super.build();
        var p = (MethodTreeBuilder) parent;
        // todo: get rid of way it is done
        if (((ClassTreeBuilder) p.getParent()).isPredefined()) return;
        // type of expression is checked when "return" is met, so if it is present - it is correct
        if (p.isTypeOf(getClass("Void"))) return;
        if (p.isConstructor()) return;
        // check that return in typed method will be met in any flow of the function execution
        if (!validate())
            throw new CompilerError("Not all flows lead to return value in method " + parent);
    }

    @Override
    protected void visitSingly(BuildTreeVisitor v) {
        v.visitBody(this);
    }
}
