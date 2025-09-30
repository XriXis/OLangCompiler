package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.CompilerError;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.StreamSupport;

public class RootTreeBuilder implements BuildTree {
    ArrayList<BuildTree> children;
    Iterator<Token> source;

    public RootTreeBuilder(Iterable<Token> stream) {
        source = StreamSupport
                .stream(stream.spliterator(), false)
                .filter((t) -> !t.isWhitespace())
                .iterator();
    }

    @Override
    public void build() {
        while (source.hasNext()) {
            children.add(scanClass());
        }
        for (var child : children)
            child.build();
    }

    private ClassTreeBuilder scanClass() {
        var classIdentifier = source.next();
        if (!(classIdentifier.entry() instanceof Keyword) || !classIdentifier.entry().equals(Keyword.CLASS))
            throw new CompilerError("Top level declarations could be only classes. Improper token met at " + classIdentifier.position());
        var classCode = new ArrayList<Token>();
        var bracesStack = new Stack<Token>();
        bracesStack.add(null);
        while (!bracesStack.empty() && source.hasNext()) {
            var cur = source.next();
            if (cur.entry().equals(Keyword.IS)) {
                bracesStack.add(cur);
            }
            if (cur.entry().equals(Keyword.END)) {
                do bracesStack.pop();
                while (bracesStack.peek() == null);
            }
            classCode.add(cur);
        }
        if (!bracesStack.empty())
            throw new CompilerError("Unclosed class declaration found at " + classIdentifier.position());
        return new ClassTreeBuilder(classCode);

    }
}
