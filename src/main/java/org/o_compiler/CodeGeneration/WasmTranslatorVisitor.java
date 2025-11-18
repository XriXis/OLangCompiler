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
import org.o_compiler.SyntaxAnalyzer.builder.Variable;

import static org.o_compiler.CodeGeneration.DeferredVisitorAction.empty;

public class WasmTranslatorVisitor implements BuildTreeVisitor {
    private final StringBuilder buffer;

    private DeferredVisitorAction append(String msg) {
        return () -> buffer.append(msg);
    }

    public WasmTranslatorVisitor() {
        buffer = new StringBuilder();
    }

    public String cumulatedFile() {
        return buffer.toString();
    }

    @Override
    public DeferredVisitorAction visitRoot(RootTreeBuilder instance) {
        String initStr =
                "(module\n" +
                        "  (memory (export \"memory\") 1)\n" +
                        "\n" +
                        "  ;; Глобальные переменные: смещения полей\n" +
                        "  (global $offset_a i32 (i32.const 0))\n" +
                        "  (global $offset_b i32 (i32.const 4))\n" +
                        "  (global $size_MyClass i32 (i32.const 8))\n" +
                        "\n" +
                        "  ;; Глобальная переменная для указателя на кучу памяти\n" +
                        "  (global $heapPtr (mut i32) (i32.const 8))\n\n";

        buffer.append(initStr);
        return append(")");
    }

    public DeferredVisitorAction visitClass(ClassTreeBuilder instance, String classname) {
        String commentStr = "  ;; class %s\n".formatted(classname);
        buffer.append(commentStr);
        return append("");
    }

    @Override
    public DeferredVisitorAction visitMethod(MethodTreeBuilder instance) {
        String methodName = instance.generateName();
        StringBuilder declarationStr = new StringBuilder("  (func $%s ".formatted(methodName));
        // if entry point, mark as _start
        if (instance.getParentName().equals("Main") && instance.getName().equals("this")) {
            declarationStr.append("(export \"_start\") ");
        }

        // parameters
        for (Variable variable : instance.getParameters()) {
            String typeStr = variable.getType() == null ?
                    variable.getPolymorphicIdentifier() :
                    variable.getType().simpleName();
            typeStr = typeStr.equals("Real") ? "f64" : "i64";

            declarationStr.append("(param $%s %s) ".formatted(variable.getName(), typeStr));
        }

        // return type
        if (instance.getType() != null) {
            String typeStr = instance.getType().toString();
            typeStr = typeStr.equals("Real") ? "f64" : "i64";
            declarationStr.append("(result %s) ".formatted(typeStr));

            // temporary pass
            if (typeStr.equals("f64")) {
                declarationStr.append("\n    (f64.const 0.0) ");
            } else {
                declarationStr.append("\n    (i64.const 0) ");
            }
        }

        buffer.append(declarationStr).append("\n");

        return append("  )\n");
    }

    @Override
    public DeferredVisitorAction visitAttribute(AttributeTreeBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitAssignment(AssignmentBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitDeclaration(DeclarationBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitReturnStatement(ReturnStatementBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitVariableValueAccess(VariableValueAccessTreeBuild instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitMethodCall(MethodCallTreeBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitLiteralAccess(LiteralAccessExpression<?> instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitEmptyExpression(EmptyExpression instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitConstructorInvocation(ConstructorInvocationTreeBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitWhile(WhileTreeBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitIf(IfTreeBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitElse(ElseBlock instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitBody(BodyTreeBuilder instance) {
        return empty;
    }
}