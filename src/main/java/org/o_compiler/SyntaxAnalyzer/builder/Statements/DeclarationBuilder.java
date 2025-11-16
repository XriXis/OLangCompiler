package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ConstructorInvocationTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Valuable;
import org.o_compiler.SyntaxAnalyzer.builder.Variable;

import java.util.List;

public class DeclarationBuilder implements TreeBuilder, Valuable {
    RevertibleStream<Token> source;
    ConstructorInvocationTreeBuilder init;
    String name;
    TreeBuilder parent;
    Variable var;

    // source - should be already a line
    public DeclarationBuilder(Iterable<Token> source, TreeBuilder parent) {
        // [var], [x], [:], [Int], ?{ [(], [5], [)] }
        this.source = new RevertibleStream<>(source.iterator(), 5);
        this.parent = parent;
    }

    @Override
    public void build() {
        var var = source.next();
        if (!var.entry().equals(Keyword.VAR))
            throw new InternalCommunicationError("Declaration must starts with var keyword. Identified declaration starts with " + var + " at " + var.position());
        var name = source.next();
        if (!(name.entry() instanceof Identifier))
            throw new CompilerError("Unexpected token met at " + name.position() + ". New identifier expected.");
        if (parent.encloseName(name.entry().value()))
            throw new CompilerError("Double declaration in same scope is prohibited");
        if (!var.entry().equals(Keyword.VAR))
            throw new InternalCommunicationError("Declaration must starts with var keyword. Identified declaration starts with " + var + " at " + var.position());
        var column = source.next();
        if (!column.entry().equals(ControlSign.COLUMN))
            throw new CompilerError("Column expected for type annotation. Unexpected token met at at " + column.position());
        var type = source.next();
        if (!(type.entry() instanceof Identifier)) {
            throw new CompilerError("Unexpected token met at " + type.position() + ". Class identifier expected.");
        }
        var typeTree = getClass(type.entry().value());
        if (typeTree == null)
            throw new CompilerError("Unexpected token met at " + type.position() + ". Class identifier expected.");
        this.name = name.entry().value();
        init = new ConstructorInvocationTreeBuilder(typeTree, source,this);
        init.build();
        this.var = new Variable(this.name, init.getType(), this);
    }

    @Override
    public TreeBuilder getParent() {
        return parent;
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return TreeBuilder.appendTo(to, depth, "Declaration of the variable " + name, List.of(init));
    }

    public String getName(){
        return name;
    }

    @Override
    public Variable getVariable() {
        return var;
    }
}
