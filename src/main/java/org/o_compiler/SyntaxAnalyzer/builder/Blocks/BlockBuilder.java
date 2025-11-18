package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.StatementScanner;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class BlockBuilder extends TreeBuilder {
    public RevertibleStream<Token> code;
    ArrayList<TreeBuilder> children = new ArrayList<>();
    HashMap<String, DeclarationBuilder> namespace = new HashMap<>();

    public BlockBuilder(Iterable<Token> source, TreeBuilder parent) {
        super(parent);
        code = new RevertibleStream<>(source.iterator(), 3);
    }

    public void build() {
        for (var token: (new IteratorSingleIterableAdapter<>(new StatementScanner(code, this)))){
            children.add(token);
            token.build();
            if (token instanceof DeclarationBuilder tokenAlias){
                namespace.put(tokenAlias.getName(), tokenAlias);
            }
            if (code.hasNext()) {
                if (code.next().entry().equals(Keyword.END)) {
                    break;
                }
                code.revert();
            }
        }
    }

    @Override
    public boolean encloseName(String name) {
        return namespace.containsKey(name);
    }

    @Override
    public DeclarationBuilder getEnclosedName(String name){
        return namespace.get(name);
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, "");
    }

    // crutch for body proper validation without code bloating
    protected boolean validate() throws CompilerError {
        for (var child: children.reversed()) {
            if (child instanceof ReturnStatementBuilder) return true;
            if (child instanceof ConditionalBlock condChild){
                if (condChild.elseBranch!=null
                        && condChild.elseBranch.validate()
                        && condChild.validate())
                    return true;
            } else if (child instanceof BlockBuilder ch){
                if (ch.validate()) return true;
            }
        }
        return false;
    }

    @Override
    public Collection<? extends TreeBuilder> children(){
        return children;
    }
}
