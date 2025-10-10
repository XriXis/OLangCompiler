package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.builder.PredefinedClasses.PredefinedClassesParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.StreamSupport;

public class RootTreeBuilder implements BuildTree {
    Iterator<Token> source;
    HashMap<String, ClassTreeBuilder> classes;

    public RootTreeBuilder(Iterable<Token> stream) {
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
    public BuildTree getParent() {
        return null;
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

    private void includePredeclaredClasses() {
        var stream = PredefinedClassesParser.getPredefinedClassesStream();

        // scan all classes and add to children
        while (stream.hasNext()) {
            var classBuilder = scanClass(stream);
            if (classBuilder != null) {
                classes.put(classBuilder.className, classBuilder);
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
                    throw  new CompilerError("Unexpected token met " + cur + " at " + cur.position());
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
            if (cur.entry().equals(Keyword.IS) || cur.entry().equals(Keyword.LOOP)) {
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
    public ClassTreeBuilder getClass(String name) {
        return getEnclosedName(name);
    }

    @Override
    public ClassTreeBuilder getEnclosedName(String name) {
        return classes.get(name);
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, "Program init", classes.values());
    }

    @Override
    public String toString(){
        return toString_();
    }

    public static void main(String[] args) {
        try {
            RootTreeBuilder rootTreeBuilder = new RootTreeBuilder(
                    new IteratorSingleIterableAdapter<>(new TokenStream(Files.newInputStream(Path.of("data/test.o")))));

            rootTreeBuilder.build();
            System.out.println(rootTreeBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
