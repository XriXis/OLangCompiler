package org.o_compiler.SyntaxAnalyzer.builder.EntityScanner;

import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public abstract class EntityScanner implements Iterator<BuildTree> {
    RevertibleStream<Token> source;
    protected EntityScanner(Iterator<Token> source){
        this.source = new RevertibleStream<>(source, 100);
    }

    protected ArrayList<Token> scanLine(){
        var res = new ArrayList<Token>();
        while (!res.getLast().entry().equals(ControlSign.END_LINE)) res.add(source.next());
        return res;
    }

    protected ArrayList<Token> scanBlock(){
        var enclosedCode = new ArrayList<Token>();
        var bracesStack = new Stack<Token>();
        bracesStack.add(null);
        while (!bracesStack.empty() && source.hasNext()) {
            var cur = source.next();
            if (cur.entry() instanceof Keyword && ((Keyword) cur.entry()).isBlockOpen()) {
                bracesStack.add(cur);
            }
            if (cur.entry().equals(Keyword.END)) {
                do bracesStack.pop();
                while (!bracesStack.empty() && bracesStack.peek() == null);
            }
            enclosedCode.add(cur);
        }
        if (!bracesStack.empty())
            throw new CompilerError("Unclosed blocking statement found. From " + enclosedCode.getFirst().position() + " to " + enclosedCode.getLast().position());
        return enclosedCode;
    }
}
