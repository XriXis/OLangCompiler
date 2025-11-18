//package org.o_compiler.CodeGeneration;
//
//import org.o_compiler.SyntaxAnalyzer.builder.Blocks.BodyTreeBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Blocks.ElseBlock;
//import org.o_compiler.SyntaxAnalyzer.builder.Blocks.IfTreeBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Blocks.WhileTreeBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Classes.AttributeTreeBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Classes.ClassTreeBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Classes.MethodTreeBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Classes.RootTreeBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Expressions.*;
//import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
//import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;
//
//import static org.o_compiler.CodeGeneration.DeferredVisitorAction.empty;
//
//public class ConstantExprReductionVisitor implements BuildTreeVisitor{
//    @Override
//    public DeferredVisitorAction visitRoot(RootTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitClass(ClassTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitMethod(MethodTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitAttribute(AttributeTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitAssignment(AssignmentBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitDeclaration(DeclarationBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitReturnStatement(ReturnStatementBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitVariableValueAccess(VariableValueAccessTreeBuild instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitMethodCall(MethodCallTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitLiteralAccess(LiteralAccessExpression<?> instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitEmptyExpression(EmptyExpression instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitConstructorInvocation(ConstructorInvocationTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitWhile(WhileTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitIf(IfTreeBuilder instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitElse(ElseBlock instance) {
//        return empty;
//    }
//
//    @Override
//    public DeferredVisitorAction visitBody(BodyTreeBuilder instance) {
//        return empty;
//    }
//}
