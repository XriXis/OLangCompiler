package org.o_compiler.CodeGeneration;

import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
import org.o_compiler.SyntaxAnalyzer.Exceptions.InternalCommunicationError;
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

import java.util.HashMap;

import static org.o_compiler.CodeGeneration.DeferredVisitorAction.empty;

public class WasmTranslatorVisitor implements BuildTreeVisitor {
    private final StringBuilder buffer;
    private final HashMap<String, Integer> staticMemoryAllocation = new HashMap<>();
    private int staticMemoryLen = 0;

    private DeferredVisitorAction append(String msg) {
        return () -> buffer.append(msg);
    }

    private final DeferredVisitorAction closeBlock = append(")");

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
            typeStr = typeStr.equals("Real") ? "f32" : "i32";

            declarationStr.append("(param $%s %s) ".formatted(variable.getName(), typeStr));
        }

        // return type
        if (instance.getType() != null) {
            String typeStr = instance.getType().toString();
            typeStr = typeStr.equals("Real") ? "f32" : "i32";
            declarationStr.append("(result %s) ".formatted(typeStr));

            // temporary pass
            if (typeStr.equals("f32")) {
                declarationStr.append("\n    (f32.const 0.0) ");
            } else {
                declarationStr.append("\n    (i32.const 0) ");
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
        var isReal = instance.getVariable().getType().isSubclassOf(RootTreeBuilder.getPredefined("Real"));
        var declarationName = "$" + instance.getName();
        buffer
                .append("(local ")
                .append(declarationName)
                .append(' ')
                .append(isReal ? "f64" : "i64")
                .append(')')
        ;
        buffer
                .append("(local.set ")
                .append(declarationName)
                .append(' ')
        ;
        return closeBlock;
    }

    @Override
    public DeferredVisitorAction visitReturnStatement(ReturnStatementBuilder instance) {
        return empty;
    }

    @Override
    public DeferredVisitorAction visitVariableValueAccess(VariableValueAccessTreeBuild instance) {
//        String accessSequence;
//        switch (instance.of()) {
//            case AttributeTreeBuilder node -> {
//                ;
//            }
//            case Variable node -> {
//                ;
//            }
//            case DeclarationBuilder node -> {
//                ;
//            }
//            default -> throw new IllegalStateException("Unexpected value: " + instance.of());
//        }
        return empty;
    }

    @Override
    public DeferredVisitorAction visitMethodCall(MethodCallTreeBuilder instance) {
        return empty;
    }

    @Override
    public <T> DeferredVisitorAction visitLiteralAccess(LiteralAccessExpression<T> instance,
                                                        ClassTreeBuilder type,
                                                        Literal<T> value) {
        // todo: delegate types differentiation into literal class(es)
        if (type == RootTreeBuilder.getPredefined("Integer")) {
            buffer.append("(i64.const ").append(value.value()).append(')');
        } else if (type == RootTreeBuilder.getPredefined("Real")) {
            buffer.append("(f64.const ").append(value.value()).append(')');
        } else if (type == RootTreeBuilder.getPredefined("Boolean")) {
            // todo: use bytes(). But for it generic recognition or implementation segregation is required
            //  --------------------------------------vvvvvvvvvvvvvvvvvvvvvv
            buffer.append("(i64.const ").append(value.value().equals("true") ? "1" : "0").append(')');
        } else if (type == RootTreeBuilder.getPredefined("String")) {
            if (!staticMemoryAllocation.containsKey(value.value())) {
                staticMemoryAllocation.put(value.value(), staticMemoryLen);
                staticMemoryLen += value.value().length();
            }
            buffer.append("(i64.const ").append(staticMemoryAllocation.get(value.value())).append(")");
        } else
            throw new InternalCommunicationError("Impossible literal type " + type.simpleName());
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
    public DeferredVisitorAction visitWhile(WhileTreeBuilder instance, ExpressionTreeBuilder condition) {
//        var loop_name = "$while_loop_" + ;
//        buffer.append()
        return empty;
    }

    @Override
    public DeferredVisitorAction visitIf(IfTreeBuilder instance, ExpressionTreeBuilder condition) {
        buffer.append("(if ");
        condition.visit(this);
        buffer.append("(then ");
        return closeBlock;
    }

    @Override
    public DeferredVisitorAction visitElse(ElseBlock instance) {
        buffer.append("(else ");
        return closeBlock;
    }

    @Override
    public DeferredVisitorAction visitBody(BodyTreeBuilder instance) {
        return empty;
    }
}
