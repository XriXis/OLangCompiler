package org.o_compiler.CodeGeneration;

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
import org.o_compiler.SyntaxAnalyzer.builder.Valuable;
import org.o_compiler.SyntaxAnalyzer.builder.Variable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.o_compiler.CodeGeneration.DeferredVisitorAction.empty;

public class WasmTranslatorVisitor implements BuildTreeVisitor {
    private final StringBuilder buffer = new StringBuilder();
    private final HashMap<String, Integer> staticMemoryAllocation = new HashMap<>();
    private int staticMemoryLen = 0;
    private final Random rnd = new Random();
    final HashSet<String> codeLabels = new HashSet<>();
    // stack allow to nest bubbling. Global declarations for class translation, locals for the methods.
    // Arrays inside mean priority (grouped order) of bubbled code
    private final Stack<ArrayList<StringBuilder>> bubbledInstructions = new Stack<>();

    private String generateUniqueCodeLabel() {
        Supplier<String> rndLabel = () -> rnd.ints('a', 'z' + 1)
                .limit(4)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        String potentialName = rndLabel.get();
        while (codeLabels.contains(potentialName))
            potentialName = rndLabel.get();
        codeLabels.add(potentialName);
        return potentialName;
    }

    private DeferredVisitorAction append(String msg) {
        return () -> buffer.append(msg);
    }

    private final DeferredVisitorAction closeBlock = append(")");

    public String cumulatedFile() {
        return buffer.toString();
    }

    @Override
    public DeferredVisitorAction visitRoot(RootTreeBuilder instance) {
        buffer.append("  ;; Generated code\n");
        return empty;
    }

    @Override
    public DeferredVisitorAction visitClass(ClassTreeBuilder instance, String classname) {
        String commentStr = "  ;; class %s\n".formatted(classname);
        buffer.append(commentStr);
        return append("\n");
    }

    @Override
    public DeferredVisitorAction visitMethod(MethodTreeBuilder instance) {
        // should be in the body, but lets assume, that it is ok to keep it here
        bubbledInstructions.push(new ArrayList<>(List.of(new StringBuilder(), new StringBuilder())));

        String methodName = instance.wasmName();
        StringBuilder declarationStr = new StringBuilder("  (func $%s ".formatted(methodName));
        // if entry point, mark as _start
        if (instance.getParentName().equals("Main") && instance.getName().equals("this")) {
            declarationStr.append("(export \"_start\") ");
        }

        var parameters = instance.getParameters();
        // add parameter this to non-constructor methods
        if (!instance.getName().equals("this")) {
            declarationStr.append("(param $%s %s) ".formatted("this", "i32"));
            // instantiate value in constructor.
        } else {
            bubbledInstructions.peek().get(0).append("(local $this i32)");
            bubbledInstructions.peek().get(1)
                    .append("(local.set $this (call $malloc (i32.const ")
                    .append(4 * instance.parent.children().stream()
                            .takeWhile((v) -> v instanceof AttributeTreeBuilder).count())
                    .append(")))\n  ");
            // todo: make wasm constructor return (local.get $this) by adding this part in the deferred action
        }
        // parameters
        for (Variable variable : parameters) {
            String typeStr = variable.getType() == null ?
                    variable.getPolymorphicIdentifier() :
                    variable.getType().simpleName();
            typeStr = typeStr.equals("Real") ? "f32" : "i32";

            declarationStr.append("(param $%s %s) ".formatted(variable.getName(), typeStr));
        }

        String res;
        // return type
        if (instance.getType() != null) {
            String typeStr = instance.getType().simpleName();
            typeStr = typeStr.equals("Real") ? "f32" : "i32";
            declarationStr.append("(result %s) ".formatted(typeStr));
            // temporary pass
            res = typeStr.equals("f32") ? "    (f32.const 0.0)\n  " : "    (i32.const 0)\n  ";
        } else {
            res = "  ";
        }

        buffer.append(declarationStr).append("\n  ");
        var cur_len = buffer.length();
        return () -> {
            if (instance.isConstructor()) {
                buffer.append("    (local.get $this)\n  ");
//                ----------------------------------v added in the deffer action od the body. Branch should not be present at all
            } else if (buffer.length() == cur_len + 6) {
                buffer.delete(buffer.length()-3, buffer.length());
                buffer.append(res);
            }
            buffer.append(")\n\n");
        };
    }

    @Override
    public DeferredVisitorAction visitAttribute(AttributeTreeBuilder instance) {
        /*
        (global        ; объявление глобальной переменной
          $Person_name_offset  ; имя: Person_name_offset
          i32                  ; тип: 32-битное целое
          (i32.const 0)        ; значение: константа 0
        )                      ; конец объявления
         */

        var offset = instance.getPos() * 4;

        String attrName = instance.wasmName();
        buffer.append("  (global $%s_offset i32 (i32.const %d))\n\n".formatted(attrName, offset));

        return empty;
    }

    @Override
    public DeferredVisitorAction visitAssignment(AssignmentBuilder instance, Valuable of) {
        // todo: make segregation encapsulated. Adjust DRY
        Consumer<Valuable> localSet = (var) ->
                buffer.append("(local.set $").append(var.getVariable().getName()).append(" ");
        switch (of) {
            case AttributeTreeBuilder node -> {
                var fieldAddrName = "$tmp_field_addr_var_" + generateUniqueCodeLabel();
                bubbledInstructions.peek().getFirst().append("(local ").append(fieldAddrName).append(" i32)");
                buffer
                        .append("(local.set ")
                        .append(fieldAddrName)
                        .append(" (i32.add (local.get $this) (global.get $").append(node.wasmName()).append("_offset)))")
                        .append("(").append(node.isTypeOf(RootTreeBuilder.getPredefined("Real")) ? 'f' : 'i')
                        .append("32.store (i32.load (local.get ").append(fieldAddrName).append(")) ")
                ;
            }
            case Variable node -> localSet.accept(node);
            case DeclarationBuilder node -> localSet.accept(node);
            default -> throw new InternalCommunicationError("Assignment is not defined for " + of);
        }
        return closeBlock;
    }

    @Override
    public DeferredVisitorAction visitDeclaration(DeclarationBuilder instance) {
        var isReal = instance.getVariable().getType().isSubclassOf(RootTreeBuilder.getPredefined("Real"));
        var declarationName = "$" + instance.getName();
        bubbledInstructions.peek().getFirst()
                .append("(local ")
                .append(declarationName)
                .append(' ')
                .append(isReal ? "f32" : "i32")
                .append(") ")
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
        return empty; // done
    }

    @Override
    public DeferredVisitorAction visitVariableValueAccess(VariableValueAccessTreeBuild instance) {
        // todo: make segregation encapsulated. Adjust DRY
        Consumer<Valuable> localAccess = (var) ->
                buffer.append("(local.get $").append(var.getVariable().getName()).append(") ");
        switch (instance.of()) {
            case AttributeTreeBuilder node -> buffer
                    .append("(")
                    .append(node.isTypeOf(RootTreeBuilder.getPredefined("Real")) ? 'f' : 'i')
                    .append("32.load (i32.add (local.get $this) (global.get $")
                    .append(node.wasmName())
                    .append("_offset))) ");

            case Variable node -> localAccess.accept(node);
            case DeclarationBuilder node -> localAccess.accept(node);
            default -> throw new InternalCommunicationError("Impossible to extract value from " + instance.of());
        }
        return empty;
    }

    @Override
    public DeferredVisitorAction visitMethodCall(MethodCallTreeBuilder instance, MethodTreeBuilder signature) {
        var callName = signature.wasmName();
        buffer.append("(call $").append(callName).append(' ');
        return closeBlock;
    }

    @Override
    public <T> DeferredVisitorAction visitLiteralAccess(LiteralAccessExpression<T> instance,
                                                        ClassTreeBuilder type,
                                                        Literal<T> value) {
        // todo: delegate types differentiation into literal class(es)
        if (type == RootTreeBuilder.getPredefined("Integer")) {
            buffer.append("(i32.const ").append(value.value()).append(") ");
        } else if (type == RootTreeBuilder.getPredefined("Real")) {
            buffer.append("(f32.const ").append(value.value()).append(") ");
        } else if (type == RootTreeBuilder.getPredefined("Boolean")) {
            // todo: use bytes(). But for it generic recognition or implementation segregation is required
            //  --------------------------------------vvvvvvvvvvvvvvvvvvvvvv
            buffer.append("(i32.const ").append(value.value().equals("true") ? "1" : "0").append(") ");
        } else if (type == RootTreeBuilder.getPredefined("String")) {
            if (!staticMemoryAllocation.containsKey(value.value())) {
                staticMemoryAllocation.put(value.value(), staticMemoryLen);
                staticMemoryLen += value.value().length();
            }
            buffer.append("(i32.const ").append(staticMemoryAllocation.get(value.value())).append(") ");
        } else
            throw new InternalCommunicationError("Impossible literal type " + type.simpleName());
        return empty;
    }

    @Override
    public DeferredVisitorAction visitEmptyExpression(EmptyExpression instance) {
        return empty; // done
    }

    @Override
    public DeferredVisitorAction visitConstructorInvocation(ConstructorInvocationTreeBuilder instance,
                                                            MethodTreeBuilder signature) {
        var callName = signature.wasmName();
        buffer.append("(call $").append(callName).append(' ');
        return closeBlock;
    }

    @Override
    public DeferredVisitorAction visitWhile(WhileTreeBuilder instance, ExpressionTreeBuilder condition, ElseBlock elseBlock) {
        var loopCode = generateUniqueCodeLabel();
        var loopBlockName = "$block_for_loop_whole_" + loopCode;
        var trueBlockName = "$block_for_loop_true_" + loopCode;
        var loopName = "$while_loop_" + loopCode;
        buffer.append("(block ").append(loopBlockName).append(' ');
        buffer.append("(block ").append(trueBlockName).append(' ');
        buffer.append("(loop ").append(loopName).append(' ');
        // ----------------vvvvvvvv condition negation
        buffer.append("(if (i32.eqz ");
        condition.visit(this);
        buffer.append(") (then (br ").append(loopBlockName).append("))) ");
        return () -> {
            buffer.append("))");
            if (elseBlock != null)
                elseBlock.visit(this);
            buffer.append(")");
        };
    }

    @Override
    public DeferredVisitorAction visitIf(IfTreeBuilder instance, ExpressionTreeBuilder condition, ElseBlock elseBlock) {
        buffer.append("(if ");
        condition.visit(this);
        buffer.append("(then ");
        return () -> {
            buffer.append(')');
            if (elseBlock != null) {
                buffer.append("(else ");
                elseBlock.visit(this);
                buffer.append(")");
            }
            buffer.append(')');
        };
    }

    @Override
    public DeferredVisitorAction visitElse(ElseBlock instance) {
        return empty; //done
    }

    @Override
    public DeferredVisitorAction visitBody(BodyTreeBuilder instance) {
        int insertionPlace = buffer.length();
        return () -> {
            var place = insertionPlace;
            for (var item: bubbledInstructions.pop()) {
                buffer.insert(place, item.append("\n  "));
                place += item.length();
            }
        };
    }
}
