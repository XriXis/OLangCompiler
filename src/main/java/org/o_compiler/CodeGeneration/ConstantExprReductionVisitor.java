package org.o_compiler.CodeGeneration;

import org.o_compiler.SyntaxAnalyzer.builder.*;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.IfTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.WhileTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.*;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;

public class ConstantExprReductionVisitor implements BuildTreeVisitor{
    @Override
    public void visitRoot(RootTreeBuilder instance) {

    }

    @Override
    public void visitClass(ClassTreeBuilder instance) {

    }

    @Override
    public void visitMethod(MethodTreeBuilder instance) {

    }

    @Override
    public void visitAttribute(AttributeTreeBuilder instance) {

    }

    @Override
    public void visitAssignment(AssignmentBuilder instance) {

    }

    @Override
    public void visitDeclaration(DeclarationBuilder instance) {

    }

    @Override
    public void visitReturnStatement(ReturnStatementBuilder instance) {

    }

    @Override
    public void visitVariableValueAccess(VariableValueAccessTreeBuild instance) {

    }

    @Override
    public void visitMethodCall(MethodCallTreeBuilder instance) {

    }

    @Override
    public void visitLiteralAccess(LiteralAccessExpression<?> instance) {

    }

    @Override
    public void visitEmptyExpression(EmptyExpression instance) {

    }

    @Override
    public void visitConstructorInvocation(ConstructorInvocationTreeBuilder instance) {

    }

    @Override
    public void visitWhile(WhileTreeBuilder instance) {

    }

    @Override
    public void visitIf(IfTreeBuilder instance) {

    }
}
