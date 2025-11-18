package org.o_compiler.CodeGeneration;

import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
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

public interface BuildTreeVisitor {
    DeferredVisitorAction visitRoot(RootTreeBuilder instance);

    DeferredVisitorAction visitClass(ClassTreeBuilder instance, String name);

    DeferredVisitorAction visitMethod(MethodTreeBuilder instance);

    DeferredVisitorAction visitAttribute(AttributeTreeBuilder instance);

    DeferredVisitorAction visitAssignment(AssignmentBuilder instance);

    DeferredVisitorAction visitDeclaration(DeclarationBuilder instance);

    DeferredVisitorAction visitReturnStatement(ReturnStatementBuilder instance);

    DeferredVisitorAction visitVariableValueAccess(VariableValueAccessTreeBuild instance);

    DeferredVisitorAction visitMethodCall(MethodCallTreeBuilder instance);

    <T> DeferredVisitorAction visitLiteralAccess(LiteralAccessExpression<T> instance, ClassTreeBuilder type, Literal<T> value);

    DeferredVisitorAction visitEmptyExpression(EmptyExpression instance);

    DeferredVisitorAction visitConstructorInvocation(ConstructorInvocationTreeBuilder instance);

    DeferredVisitorAction visitWhile(WhileTreeBuilder instance, ExpressionTreeBuilder condition);

    DeferredVisitorAction visitIf(IfTreeBuilder instance, ExpressionTreeBuilder condition);

    DeferredVisitorAction visitElse(ElseBlock instance);

    DeferredVisitorAction visitBody(BodyTreeBuilder instance);
}
