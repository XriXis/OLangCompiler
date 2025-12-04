package org.o_compiler.SyntaxAnalyzer.builder.Expressions;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.RevertibleStream;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.Exceptions.UndefinedCallError;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.ClassTreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.ArgsParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ConstructorInvocationTreeBuilder extends CallExpressionTreeBuilder {
    RevertibleStream<Token> unparsedArgs;

    // callSource - [(], ?{[arg1 expr], [arg2 expr] ...}, [)] || empty
    public ConstructorInvocationTreeBuilder(ClassTreeBuilder type, RevertibleStream<Token> callSource, TreeBuilder parent) {
        super(parent);
        this.type = type;
        unparsedArgs = callSource;
    }

    public ConstructorInvocationTreeBuilder(ClassTreeBuilder type, Iterable<Token> callSource, TreeBuilder parent) {
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
            if (!this.unparsedArgs.hasNext())
                this.args = new ArrayList<>();
            else
                this.args = new ArgsParser(unparsedArgs, this).get();
        } else {
            unparsedArgs.revert();
            this.args = new ArgsParser(unparsedArgs, this).get();
        }
        var types = args.stream().map(ExpressionTreeBuilder::getType).toList();
        this.it = type.getMethod("this", types);
        if (it == null) {
            throw new UndefinedCallError("Undefined constructor of " + type.simpleName() + " with arguments: (" + types + ")");
        }
        super.build();
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        return appendTo(to, depth, it + " call");
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitConstructorInvocation(this, it);
    }

    @Override
    public Collection<? extends TreeBuilder> children() {
        return args;
    }

    @Override
    public String toString() {
        return "Call to constructor of " + type.simpleName();
    }
}
