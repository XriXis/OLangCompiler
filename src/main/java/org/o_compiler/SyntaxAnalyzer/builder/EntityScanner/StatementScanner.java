package org.o_compiler.SyntaxAnalyzer.builder.EntityScanner;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.*;
import org.o_compiler.SyntaxAnalyzer.builder.CodeBuilders.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.CodeBuilders.IfTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.CodeBuilders.ReturnStatementBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.CodeBuilders.WhileTreeBuilder;

import java.util.Iterator;

public class StatementScanner extends EntityScanner{
    public StatementScanner(Iterator<Token> source) {
        super(source);
    }

    @Override
    public boolean hasNext() {
        Token token;
        do {
            if (!source.hasNext()) return false;
            token = source.next();
        }
        while (token.entry().equals(ControlSign.END_LINE));
        source.revert();
        return source.hasNext();
    }

    @Override
    public BuildTree next() {
        var token = source.next();
        source.revert();
        if (token.entry().equals(ControlSign.END_LINE)) return next();
        if (token.entry().equals(Keyword.VAR)) return new DeclarationBuilder(scanLine());
        if (token.entry().equals(Keyword.IF)) return new IfTreeBuilder(scanBlock());
        if (token.entry().equals(Keyword.WHILE)) return new WhileTreeBuilder(scanBlock());
        if (token.entry().equals(Keyword.RETURN)) return new ReturnStatementBuilder(scanLine());
        return new AssignmentBuilder(scanLine());
    }
}
