package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.parser.TokenStream;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;

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
            var classBuilder = scanClass();
//            System.out.println(classBuilder.className);
//            for (Iterator<Token> it = classBuilder.source; it.hasNext(); ) {
//                var token = it.next();
//                System.out.println(token + " ");
//            }
//            System.out.println();
            classes.put(classBuilder.className, classBuilder);
        }
        // scan all class members
        for (var classBuilder : classes.values()) {
            classBuilder.scanClassMembers();
        }
        // build all class members
//        for (var classBuilder : classes.values()) {
//            classBuilder.buildClassMembers();
//        }
    }

    private void includePredeclaredClasses() {
        classes.put("Integer", new ClassTreeBuilder("Integer", new ArrayList<Token>(), this));
        classes.put("Real", new ClassTreeBuilder("Real", new ArrayList<Token>(), this));
        classes.put("Boolean", new ClassTreeBuilder("Boolean", new ArrayList<Token>(), this));
        classes.put("Array", new ClassTreeBuilder("Array", new ArrayList<Token>(), this));
        classes.put("List", new ClassTreeBuilder("List", new ArrayList<Token>(), this));
        classes.put("Void", new ClassTreeBuilder("Void", new ArrayList<Token>(), this));
        classes.put("Console", new ClassTreeBuilder("Void", new ArrayList<Token>(), this));
    }

    private ClassTreeBuilder scanClass() {
        // TODO: inheritance

        // get class identifier
        var classIdentifier = source.next();
        // ensure identifier not \n
        // TODO comment
        while (classIdentifier.entry().equals(ControlSign.END_LINE))
            classIdentifier = source.next();

        // ensure identifier is class
        if (!(classIdentifier.entry() instanceof Keyword) || !classIdentifier.entry().equals(Keyword.CLASS))
            throw new CompilerError("Top level declarations could be only classes. Improper token " + classIdentifier.entry().value() + " met at " + classIdentifier.position());

        // get class name
        Token tokenClassName = source.next();
        String stringClassName = tokenClassName.entry().value();
        // ensure class name is identifier
        if (!(tokenClassName.entry() instanceof Identifier)) {
            throw new CompilerError("Class name expected at " + tokenClassName.position() + ", got " + tokenClassName.toString());
        } else if (getClass(stringClassName) != null) {
            throw new CompilerError("Class name " + stringClassName + " already exists");
        }

        // internal class code
        var classCode = new ArrayList<Token>();
        // stack for class boundaries
        var bracesStack = new Stack<Token>();
        bracesStack.add(null);
        while (!bracesStack.empty() && source.hasNext()) {
            var cur = source.next();
            if (cur.entry().equals(Keyword.IS) || cur.entry().equals(Keyword.LOOP)) {
                bracesStack.add(cur);
            }
            if (cur.entry().equals(Keyword.END)) {
                do bracesStack.pop();
                while (!bracesStack.empty() && bracesStack.peek() == null);
            }
            classCode.add(cur);
        }
        // remove first is and last end
        classCode.removeFirst();
        classCode.removeLast();

        // check for all closed braces
        if (!bracesStack.empty())
            throw new CompilerError("Unclosed class declaration found at " + classIdentifier.position());
        // return new class
        return new ClassTreeBuilder(stringClassName, classCode, this);
    }

    @Override
    public ClassTreeBuilder getClass(String name) {
        return getEnclosedName(name);
    }

    @Override
    public ClassTreeBuilder getEnclosedName(String name) {
        return classes.get(name);
    }

    public static void main(String[] args) {
        try {
            RootTreeBuilder rootTreeBuilder = new RootTreeBuilder(
                    new IteratorSingleIterableAdapter<>(new TokenStream(Files.newInputStream(Path.of("data/test.o")))));

            rootTreeBuilder.build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
