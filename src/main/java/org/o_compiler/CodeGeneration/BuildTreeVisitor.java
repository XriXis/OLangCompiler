package org.o_compiler.CodeGeneration;

import org.o_compiler.SyntaxAnalyzer.builder.*;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.IfTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.WhileTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.*;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;

public interface BuildTreeVisitor {
    // todo remove, when end with project
    default void visit(TreeBuilder node) {
        switch (node) {
            case RootTreeBuilder instance -> visitRoot(instance);
            case ClassTreeBuilder instance -> visitClass(instance);
            case MethodTreeBuilder instance -> visitMethod(instance);
            case AttributeTreeBuilder instance -> visitAttribute(instance);
            case AssignmentBuilder instance -> visitAssignment(instance);
            case DeclarationBuilder instance -> visitDeclaration(instance);
            case ReturnStatementBuilder instance -> visitReturnStatement(instance);
            case VariableValueAccessTreeBuild instance -> visitVariableValueAccess(instance);
            case MethodCallTreeBuilder instance -> visitMethodCall(instance);
            case LiteralAccessExpression<?> instance -> visitLiteralAccess(instance);
            case EmptyExpression instance -> visitEmptyExpression(instance);
            case ConstructorInvocationTreeBuilder instance -> visitConstructorInvocation(instance);
            case WhileTreeBuilder instance -> visitWhile(instance);
            case IfTreeBuilder instance -> visitIf(instance);
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
    }

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
}
