package org.o_compiler.CodeGeneration;

import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.ElseBlock;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.IfTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.WhileTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.AttributeTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.ClassTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.MethodTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.RootTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.*;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;

public class WasmTranslatorVisitor implements BuildTreeVisitor {
    private final StringBuilder buffer;

    public WasmTranslatorVisitor() {
        buffer = new StringBuilder();
    }

    public String cumulatedFile() {
        return buffer.toString();
    }

    @Override
    public DeferredVisitorAction visitRoot(RootTreeBuilder instance) {
        buffer.append("(module\n");
        return () -> buffer.append(")");
    }

    @Override
    public DeferredVisitorAction visitClass(ClassTreeBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitMethod(MethodTreeBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitAttribute(AttributeTreeBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitAssignment(AssignmentBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitDeclaration(DeclarationBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitReturnStatement(ReturnStatementBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitVariableValueAccess(VariableValueAccessTreeBuild instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitMethodCall(MethodCallTreeBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitLiteralAccess(LiteralAccessExpression<?> instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitEmptyExpression(EmptyExpression instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitConstructorInvocation(ConstructorInvocationTreeBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitWhile(WhileTreeBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitIf(IfTreeBuilder instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitElse(ElseBlock instance) {
        return null;
    }

    @Override
    public DeferredVisitorAction visitBody(BodyTreeBuilder instance) {
        return null;
    }
}
