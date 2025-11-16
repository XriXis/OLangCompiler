package org.o_compiler.CodeGeneration;

import org.o_compiler.SyntaxAnalyzer.builder.*;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.ElseBlock;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.IfTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.WhileTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.*;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;

public interface BuildTreeVisitor {
    void visitRoot(RootTreeBuilder instance);

    void visitClass(ClassTreeBuilder instance);

    void visitMethod(MethodTreeBuilder instance);

    void visitAttribute(AttributeTreeBuilder instance);

    void visitAssignment(AssignmentBuilder instance);

    void visitDeclaration(DeclarationBuilder instance);

    void visitReturnStatement(ReturnStatementBuilder instance);

    void visitVariableValueAccess(VariableValueAccessTreeBuild instance);

    void visitMethodCall(MethodCallTreeBuilder instance);

    void visitLiteralAccess(LiteralAccessExpression<?> instance);

    void visitEmptyExpression(EmptyExpression instance);

    void visitConstructorInvocation(ConstructorInvocationTreeBuilder instance);

    void visitWhile(WhileTreeBuilder instance);

    void visitIf(IfTreeBuilder instance);

    void visitElse(ElseBlock instance);

    void visitBody(BodyTreeBuilder instance);
}
