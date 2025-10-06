package org.o_compiler.SyntaxAnalyzer.builder.Blocks;

import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.StatementScanner;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockBuilder implements BuildTree {
    RevertibleStream<Token> code;
    ArrayList<BuildTree> children = new ArrayList<>();
    HashMap<String, DeclarationBuilder> namespace = new HashMap<>();
    BuildTree parent;

    public BlockBuilder(Iterable<Token> source, BuildTree parent) {
        code = new RevertibleStream<>(source.iterator(), 3);
        this.parent = parent;
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
    public BuildTree getParent(){
        return parent;
    }
}
