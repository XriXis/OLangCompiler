package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;

import java.util.Iterator;

public class AssignmentBuilder implements BuildTree {
    Iterator<Token> code;
    ExpressionTreeBuilder value;
    DeclarationBuilder var;
    BuildTree parent;
    public AssignmentBuilder(Iterable<Token> source, BuildTree parent){
        code = source.iterator();
        this.parent = parent;
    }
    @Override
    public void build() {
        var variableToken = code.next();
        if (!(variableToken.entry() instanceof Identifier))
            throw new InternalCommunicationError("Attempt to parse assignment with no identifier at " + variableToken.position());
        var name = findNameAbove(variableToken.entry().value());
        if (name == null)
            throw new CompilerError("Assignment to undeclared variable " + variableToken.entry().value() + " at " + variableToken.position());
        // todo: related to "todo" block above. (High Coupling architecture consequences)
        if (!(name instanceof DeclarationBuilder))
            throw new CompilerError("Attempt of assignment to not-value entity at " + variableToken.position());
        var assignmentSign = code.next();
        if (!(assignmentSign.entry().equals(ControlSign.ASSIGN)))
            throw new InternalCommunicationError("Attempt to parse assignment with no assignment sign at " + assignmentSign.position());
        value = ExpressionTreeBuilder.expressionFactory(code, this);
    }


    @Override
    public BuildTree getParent() {
        return parent;
    }
}
