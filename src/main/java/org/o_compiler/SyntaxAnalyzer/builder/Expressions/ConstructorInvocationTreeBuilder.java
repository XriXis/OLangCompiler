package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.UndefinedCallError;
import org.o_compiler.SyntaxAnalyzer.builder.BuildTree;
import org.o_compiler.SyntaxAnalyzer.builder.ClassTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.ArgsParser;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConstructorInvocationTreeBuilder extends CallExpressionTreeBuilder {
    RevertibleStream<Token> unparsedArgs;

    // callSource - [(], ?{[arg1 expr], [arg2 expr] ...}, [)] || empty
    public ConstructorInvocationTreeBuilder(ClassTreeBuilder type, RevertibleStream<Token> callSource, BuildTree parent) {
        this.parent = parent;
        this.type = type;
        unparsedArgs = callSource;
    }

    public ConstructorInvocationTreeBuilder(ClassTreeBuilder type, Iterable<Token> callSource, BuildTree parent) {
        this(type, new RevertibleStream<>(callSource.iterator(), 5), parent);
    }

    @Override
    public void build() {
        if (!this.unparsedArgs.hasNext())
            this.args = new ArrayList<>();
        else if (unparsedArgs.next().entry().equals(ControlSign.BRACKET_OPEN)) {
            var genTypes = new ArrayList<Token>();
            Token cur;
            do {
                cur = unparsedArgs.next();
                if (!(cur.entry() instanceof Identifier))
                    throw new CompilerError("Improper generic syntax usage. Generic syntax should be ClassName[Identifier], found ClassName[" + cur.entry().getClass().getSimpleName() + "] at " + cur.position());
                genTypes.add(cur);
                if (!unparsedArgs.hasNext()) break;
                cur = unparsedArgs.next();
            } while (unparsedArgs.hasNext() && cur.entry().equals(ControlSign.SEPARATOR));
            if (!cur.entry().equals(ControlSign.BRACKET_CLOSED))
                throw new CompilerError("Unclosed generic description met at " + cur.position());
            type = type.initGenericClass(genTypes);
        } else {
            unparsedArgs.revert();
            this.args = new ArgsParser(unparsedArgs, this).get();
        }
        this.it = type.getMethod(
                "this",
                args.stream().map(ExpressionTreeBuilder::getType).collect(Collectors.toList())
        );
        if (it == null) {
            throw new UndefinedCallError("Undefined constructor of " + type.simpleName() + " with arguments: (" + args + ")");
        }
        super.build();
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return BuildTree.appendTo(to, depth, it + " call", args);
    }

    @Override
    public String toString() {
        return "Call to constructor of " + type.simpleName();
    }
}
