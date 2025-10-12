package org.o_compiler.SyntaxAnalyzer.builder.EntityScanner;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.UnclosedBlockException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Predicate;

public class CodeSegregator {
    RevertibleStream<Token> source;

    public CodeSegregator(Iterator<Token> source){
        this.source = new RevertibleStream<>(source, 100);
    }

    public ArrayList<Token> scanLine() {
        var res = new ArrayList<Token>();
        while (!res.getLast().entry().equals(ControlSign.END_LINE)) res.add(source.next());
        return res;
    }

    public ArrayList<Token> scanControlBlock() {
        return scanFreeBlock(
                (cur) -> cur instanceof Keyword && ((Keyword) cur).isBlockOpen(),
                (cur) -> cur.equals(Keyword.END)
        );
    }

    // smth.m().g(
    // ).f(
    // ).k().b() - one braces expr
    public ArrayList<Token> scanBracesExpr() {
        return scanFreeBlock(
                (cur) -> cur.equals(ControlSign.PARENTHESIS_OPEN),
                (cur) -> cur.equals(ControlSign.PARENTHESIS_CLOSED)
        );
    }

    // if open - "(", closed - ")", end - "\n"
    // valid sequence - () () (()(())()) (((())))\n
    public ArrayList<Token> scanFreeBlock(Predicate<TokenValue> isOpen,
                                          Predicate<TokenValue> isClosed,
                                          Predicate<TokenValue> isEnd) {
        var enclosedCode = new ArrayList<Token>();
        var bracesStack = new Stack<Token>();
        bracesStack.add(null);
        while (!bracesStack.empty() && source.hasNext()) {
            var cur = source.next();
            if (isOpen.test(cur.entry()))
                bracesStack.add(cur);
            if (isClosed.test(cur.entry())) {
                if (bracesStack.peek() == null)
                    throw new UnclosedBlockException("Closing unopened block found at " + cur.position());
                bracesStack.pop();
            }
            if (isEnd.test(cur.entry()) && bracesStack.peek() == null) {
                return enclosedCode;
            }
            enclosedCode.add(cur);
        }
        if (!bracesStack.empty() && bracesStack.peek() != null)
            throw new CompilerError("Unclosed blocking statement found. From " + enclosedCode.getFirst().position() + " to " + enclosedCode.getLast().position());
        return enclosedCode;
    }

    private ArrayList<Token> scanFreeBlock(Predicate<TokenValue> isOpen, Predicate<TokenValue> isClosed){
        return scanFreeBlock(isOpen, isClosed, (v)->v.equals(ControlSign.END_LINE));
    }
}
