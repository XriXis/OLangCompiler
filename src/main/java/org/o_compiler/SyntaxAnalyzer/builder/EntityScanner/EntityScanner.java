package org.o_compiler.SyntaxAnalyzer.builder.EntityScanner;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public class EntityScanner  extends CodeSegregator{
    BuildTree context;

    public EntityScanner(Iterator<Token> source, BuildTree context) {
        super(source);
        this.context = context;
    }

    public ArrayList<ArrayList<Token>> scanChain(Predicate<TokenValue> end) {
        var iterator = new EntityScanner(source, context);
        ArrayList<Token> cur;
        var callChain = new ArrayList<ArrayList<Token>>();
        do {
            cur = iterator
                    .scanFreeBlock(
                            (val) -> val.equals(ControlSign.PARENTHESIS_OPEN),
                            (val) -> val.equals(ControlSign.PARENTHESIS_CLOSED),
                            end
                    );
            if (!cur.isEmpty()) {
                if (cur.getLast().entry().equals(ControlSign.END_LINE))
                    cur.removeLast();
                callChain.add(cur);
            }
        } while (!cur.isEmpty());
        return callChain;
    }
}
