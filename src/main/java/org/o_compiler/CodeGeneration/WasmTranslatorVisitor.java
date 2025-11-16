package org.o_compiler.CodeGeneration;

import org.o_compiler.SyntaxAnalyzer.builder.*;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.IfTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Blocks.WhileTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Expressions.*;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.AssignmentBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.DeclarationBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Statements.ReturnStatementBuilder;

public class WasmTranslatorVisitor implements BuildTreeVisitor{
    @Override
    public void visit(TreeBuilder node) {
        switch (node){
            case RootTreeBuilder instance -> {}
            case ClassTreeBuilder instance -> {}
            case MethodTreeBuilder instance -> {}
            case AttributeTreeBuilder instance -> {}
            case AssignmentBuilder instance -> {}
            case DeclarationBuilder instance -> {}
            case ReturnStatementBuilder instance -> {}
            case VariableValueAccessTreeBuild instance -> {}
            case MethodCallTreeBuilder instance -> {}
            case LiteralAccessExpression<?> instance -> {}
            case EmptyExpression instance -> {}
            case ConstructorInvocationTreeBuilder instance -> {}
            case WhileTreeBuilder instance -> {}
            case IfTreeBuilder instance -> {}
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
    }
}
