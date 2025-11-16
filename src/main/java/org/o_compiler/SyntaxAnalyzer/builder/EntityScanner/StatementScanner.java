package org.o_compiler.SyntaxAnalyzer.builder.EntityScanner;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.*;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.IfTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.WhileTreeBuilder;

import java.util.Iterator;

public class StatementScanner extends EntityScanner implements Iterator<TreeBuilder> {
    public StatementScanner(Iterator<Token> source, TreeBuilder from) {
        super(source, from);
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
    public TreeBuilder next() {
        var token = source.next();
        source.revert();
        return switch (token.entry()) {
            case ControlSign.END_LINE -> next();
            case Keyword.VAR -> new DeclarationBuilder(scanBracesExpr(), context);
            case Keyword.IF -> new IfTreeBuilder(scanControlBlock(), context);
            case Keyword.WHILE -> new WhileTreeBuilder(scanControlBlock(), context);
            case Keyword.RETURN -> new ReturnStatementBuilder(scanBracesExpr(), context);
            default -> freeStatementCategorize();
        };
    }

    private TreeBuilder freeStatementCategorize() {
        source.next();
        var sign = source.next().entry();
        source.revert();
        source.revert();
        return sign.equals(ControlSign.ASSIGN)
                ? new AssignmentBuilder(scanBracesExpr(), context)
                : ExpressionTreeBuilder.expressionFactory(scanBracesExpr().iterator(), context);
    }
}
