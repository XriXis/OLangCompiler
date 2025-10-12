package org.o_compiler.SyntaxAnalyzer.builder.Statements;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.ExpressionTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Valuable;

import java.util.Iterator;
import java.util.List;

public class AssignmentBuilder implements BuildTree {
    Iterator<Token> code;
    ExpressionTreeBuilder value;
    Valuable var;
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
        if (!(name instanceof Valuable))
            throw new CompilerError("Attempt of assignment to not-value entity at " + variableToken.position());
        var = (Valuable) name;
        var assignmentSign = code.next();
        if (!(assignmentSign.entry().equals(ControlSign.ASSIGN))) {
            throw new InternalCommunicationError("Attempt to parse assignment with no assignment sign at " + assignmentSign.position());
        }
        value = ExpressionTreeBuilder.expressionFactory(code, this);
        if (!value.getType().isSubclassOf(var.getVariable().getType())){
            throw new CompilerError("Improper assignment. Variable " + var.getVariable().getName() + " of type " + var.getVariable().getType() + " tried to be assigned with value of type " + value.getType() + " at " + assignmentSign.position());
        }
    }


    @Override
    public BuildTree getParent() {
        return parent;
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, "Assignment value to the " + var.getVariable(), List.of(value));
    }
}
