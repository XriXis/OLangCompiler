package org.o_compiler.SyntaxAnalyzer.builder.Classes;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.PredefinedClasses.PredefinedClassesParser;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Variable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.StreamSupport;

public class RootTreeBuilder extends TreeBuilder {
    Iterator<Token> source;
    HashMap<String, ClassTreeBuilder> classes;
    static HashMap<String, ClassTreeBuilder> predefined = new HashMap<>();
    ArrayList<String> vtable = new ArrayList<>();


    static public ClassTreeBuilder getPredefined(String name) {
        return predefined.get(name);
    }

    public RootTreeBuilder(Iterable<Token> stream) {
        super(null);
        source = StreamSupport
                .stream(stream.spliterator(), false)
                .filter((t) -> !t.isWhitespace())
                .iterator();
        classes = new HashMap<>();
    }

    public boolean encloseName(String name) {
        return classes.containsKey(name);
    }

    @Override
    public void build() {
        // include predeclared classes
        includePredeclaredClasses();

        // scan all classes and add to children
        while (source.hasNext()) {
            var classBuilder = scanClass(source);
            if (classBuilder != null) {
                classes.put(classBuilder.className, classBuilder);
            }
        }
        // scan all class members
        for (var classBuilder : classes.values()) {
            classBuilder.scanClassMembers();
        }
        // build all class members
        for (var classBuilder : classes.values()) {
            classBuilder.build();
        }
    }

    // todo: segregate to getter and builder in proper place (WASM translator)
    public String buildVTable() {
        ArrayList<String> virtualMethods = new ArrayList<>();
        HashMap<String, String> types = new HashMap<>();

        // collect all data
        for (var classBuilder : classes.values()) {
            for (var classMember : classBuilder.classMembers) {
                if (classMember instanceof MethodTreeBuilder method) {
                    if (method.isOverridden || !method.overriddenMethods.isEmpty()) {
                        // assign base index to classBuilder
                        if (classBuilder.baseIndexInVTable == null) {
                            classBuilder.baseIndexInVTable = vtable.size();
                        }

                        // add implementation
                        vtable.add(method.wasmName());
                        // add type to HashMap
                        types.putIfAbsent(method.name, generateTypeAnnotation(method));
                    }
                    if (!method.overriddenMethods.isEmpty()) {
                        // add virtual
                        virtualMethods.add(method.wasmName() + "_virtual");
                    }
                }
            }
        }

        // table definition
        var res = new StringBuilder();
        res.append("  (table $vtable %d funcref)\n\n".formatted(vtable.size()));

        // vtable
        res.append("  (elem (i32.const 0) funcref\n");
        for (int i = 0; i < vtable.size(); i++) {
            res.append("    (ref.func $%s)  ;; %d\n".formatted(vtable.get(i), i));
        }
        res.append("  )\n\n");

        // types
        res.append("  ;; types\n");
        for (var type: types.values()) {
            res.append(type);
        }
        res.append("\n");

        return res.toString();
    }

    private String generateTypeAnnotation(MethodTreeBuilder methodTreeBuilder) {
        String methodName = methodTreeBuilder.name;
        StringBuilder declarationStr = new StringBuilder("  (type $%s (func ".formatted(methodName));

        // parameters
        declarationStr.append("(param i32) "); // this
        var parameters = methodTreeBuilder.getParameters();
        for (Variable variable : parameters) {
            String typeStr = variable.getType() == null ?
                    variable.getGenericIdentifier() :
                    variable.getType().simpleName();
            typeStr = typeStr.equals("Real") ? "f32" : "i32";

            declarationStr.append("(param %s) ".formatted(typeStr));
        }

        String res;
        // return type
        if (methodTreeBuilder.getType() != null && !methodTreeBuilder.getType().simpleName().equals("Void")) {
            String typeStr = methodTreeBuilder.getType().simpleName();
            typeStr = typeStr.equals("Real") ? "f32" : "i32";
            res = "(result %s) ".formatted(typeStr);
            // temporary pass
//            res = typeStr.equals("f32") ? "    (f32.const 0.0)  " : "    (i32.const 0)\n  ";
        } else {
            res = "  ";
        }

        declarationStr.append(res).append(") )\n");
        return declarationStr.toString();
    }

    public int getVTableIndex(String name) {
        return vtable.indexOf(name);
    }

    private void includePredeclaredClasses() {
        var stream = PredefinedClassesParser.getPredefinedClassesStream();

        // scan all classes and add to children
        while (stream.hasNext()) {
            var classBuilder = scanClass(stream);
            if (classBuilder != null) {
                classes.put(classBuilder.className, classBuilder);
                if (!predefined.containsKey(classBuilder.className))
                    predefined.put(classBuilder.className, classBuilder);
            }
        }
        // scan all class members
        for (var classBuilder : classes.values()) {
            classBuilder.scanClassMembers();
        }
    }

    private ClassTreeBuilder scanClass(Iterator<Token> source) {
        // get class identifier
        var classIdentifier = source.next();
        // ensure identifier not \n
        // TODO comment
        while (classIdentifier.entry().equals(ControlSign.END_LINE) && source.hasNext())
            classIdentifier = source.next();
        if (!source.hasNext()) {
            return null;
        }

        // ensure identifier is class
        if (!(classIdentifier.entry() instanceof Keyword) || !classIdentifier.entry().equals(Keyword.CLASS))
            throw new CompilerError("Top level declarations could be only classes. Improper token " + classIdentifier.entry().value() + " met at " + classIdentifier.position());

        // get class name
        Token tokenClassName = source.next();
        String stringClassName = tokenClassName.entry().value();
        // ensure class name is identifier
        if (!(tokenClassName.entry() instanceof Identifier)) {
            throw new CompilerError("Class name expected at " + tokenClassName.position() + ", got " + tokenClassName);
        } else if (getClass(stringClassName) != null) {
            throw new CompilerError("Class name " + stringClassName + " already exists");
        }
        // check for polymorphism
        var cur = source.next();
        var polymorphicClasses = new ArrayList<Token>();
        if (cur.entry().equals(ControlSign.BRACKET_OPEN)) {
            while (!cur.entry().equals(ControlSign.BRACKET_CLOSED)) {
                cur = source.next();
                if (!(cur.entry() instanceof Identifier)) {
                    throw new CompilerError("Class name expected at " + cur.position() + ", got " + cur);
                } else if (encloseName(cur.entry().value()) || polymorphicClasses.contains(cur)) {
                    throw new CompilerError("Class name " + cur.entry().value() + " already exists");
                }
                polymorphicClasses.add(cur);
                cur = source.next();
                if (cur.entry().equals(ControlSign.SEPARATOR)) {
                    cur = source.next();
                } else if (!cur.entry().equals(ControlSign.BRACKET_CLOSED)) {
                    throw new CompilerError("Unexpected token met " + cur + " at " + cur.position());
                }
            }
            cur = source.next();
        }


        // check for inheritance
        Token inheritedClassName = null;
        if (cur.entry().equals(Keyword.EXTENDS)) {
            inheritedClassName = source.next();
            if (!(inheritedClassName.entry() instanceof Identifier)) {
                throw new CompilerError("Class name expected at " + inheritedClassName.position() + ", got " + inheritedClassName);
            }
            cur = source.next();
        }

        // internal class code
        var classCode = new ArrayList<Token>();
        // stack for class boundaries
        var bracesStack = new Stack<Token>();
        bracesStack.add(null);
        while (!bracesStack.empty() && source.hasNext()) {
            if (cur.entry() instanceof Keyword c && c.isBlockOpen()) {
                bracesStack.add(cur);
            }
            if (cur.entry().equals(Keyword.END)) {
                do bracesStack.pop();
                while (!bracesStack.empty() && bracesStack.peek() == null);
            }
            classCode.add(cur);
            cur = source.next();
        }
        // remove first is and last end
        classCode.removeFirst();
        classCode.removeLast();

        // check for all closed braces
        if (!bracesStack.empty()) {
            throw new CompilerError("Unclosed class declaration found at " + classIdentifier.position());
        }
        // return new class
        return new ClassTreeBuilder(stringClassName, classCode, this, inheritedClassName, polymorphicClasses);
    }

    @Override
    public ClassTreeBuilder getEnclosedName(String name) {
        return classes.get(name);
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, "Program init");
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitRoot(this);
    }

    private List<String> generateExcludedClasses() {
        ArrayList<String> excludedClasses = new ArrayList<>(Arrays.asList("Integer", "Boolean", "Real", "Console", "Void", "Array_Integer"));
        for (var c : this.classes.values()) {
            if (c.isGeneric()) {
                excludedClasses.add(c.simpleName());
            }
        }
        return excludedClasses;
    }

    protected Map<String, String> generateGenericCallReplaceMap() {
        Map<String, String> genericCallReplaceMap = new HashMap<>();
        ArrayList<String> excludedGenericClasses = new ArrayList<>();
        for (var c : this.classes.values()) {
            if (c.isGeneric()) {
                excludedGenericClasses.add(c.simpleName());
            }
        }

        for (var excludedGenericClass : excludedGenericClasses) {
            for (var c : this.classes.values()) {
                if (c.simpleName().contains(excludedGenericClass)) {
                    genericCallReplaceMap.put(c.simpleName(), excludedGenericClass);
                }
            }
        }

        return genericCallReplaceMap;
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        List<String> excludedClasses = generateExcludedClasses();
        return this.classes.values().stream().filter(v -> !excludedClasses.contains(v.className)).toList();
    }

    public String viewWithoutPredefined() {
        return new ViewWrapper(this).toString();
    }

    @Override
    public String toString() {
        return toString_();
    }

    public static void main(String[] args) {
        RootTreeBuilder rootTreeBuilder = null;
        try {
            rootTreeBuilder = new RootTreeBuilder(
                    new IteratorSingleIterableAdapter<>(new TokenStream(Files.newInputStream(Path.of("data/test.o")))));

            rootTreeBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert rootTreeBuilder != null;
            System.out.println(rootTreeBuilder.viewWithoutPredefined());
        }
    }

    private static class ViewWrapper extends RootTreeBuilder {
        public ViewWrapper(RootTreeBuilder o) {
            super(new IteratorSingleIterableAdapter<>(o.source));
            classes = o.classes;
        }

        @Override
        public Collection<? extends TreeBuilder> children() {
            return super.classes.values().stream().filter(v -> !predefined.containsKey(v.className)).toList();
        }
    }
}

