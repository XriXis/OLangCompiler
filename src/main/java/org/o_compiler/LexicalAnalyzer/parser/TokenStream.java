package org.o_compiler.LexicalAnalyzer.parser;

import org.o_compiler.IteratorSingleIterableAdapter;
import org.o_compiler.LexicalAnalyzer.parser.FSM.FSM;
import org.o_compiler.LexicalAnalyzer.parser.FSM.NDFSM;
import org.o_compiler.LexicalAnalyzer.parser.FSM.TraverseIterator;
import org.o_compiler.LexicalAnalyzer.tokens.Span;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.IdentifierDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.LiteralType;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.Pair;
import org.o_compiler.RevertibleStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class TokenStream implements Iterator<Token> {
    private final RevertibleStream<Character> source;
    private final FSM<Function<String, TokenValue>> machine;
    private Span pos = new Span(1, 0);

    public TokenStream(final InputStream target) {
        this(new InputIterator(target));
    }

    public TokenStream(Iterable<Character> input) {
        this(input.iterator());
    }

    private TokenStream(Iterator<Character> input){
        source = new RevertibleStream<>(input, 5);
        final HashMap<String, Pair<Integer, Function<String, TokenValue>>> configuration = new HashMap<>();
        // todo: change this explicit enumeration of the classes to "All inheritors of the TokenDescriptor"
        final ArrayList<TokenDescription> types = new ArrayList<>();
        types.addAll(List.of(LiteralType.values()));
        types.addAll(List.of(ControlSign.values()));
        types.addAll(List.of(Keyword.values()));
        types.add(new IdentifierDescription());

        for (var type : types) {
            configuration.put(type.pattern(), new Pair<>(type.priority(), type::corresponding));
        }
        machine = NDFSM.fromRegexes(configuration);
    }

    @Override
    public boolean hasNext() {
        if (!source.hasNext() && source.lastRead() != '\n')
            source.imitateNext('\n');
        return source.hasNext();
    }

    @Override
    public Token next() {
        var regexEngine = machine.traverse();
        Function<String, TokenValue> lastSeen = regexEngine.result();
        while (source.hasNext()){
            var c = source.next();
            if (!source.hasNext() && source.lastRead() != '\n')
                source.imitateNext('\n');
            regexEngine.feed(c);
            if (regexEngine.isOnlyGarbage()) {
                source.revert();
                return extract(lastSeen, regexEngine);
            }
            // this change poorly influence on performance, but improve readability
            pos = pos.feed(c);
            lastSeen = regexEngine.result();
        }
        return extract(lastSeen, regexEngine);
    }

    private Token extract(
            Function<String, TokenValue> lastSeen,
            TraverseIterator<Function<String, TokenValue>> regexEngine) {
        if (lastSeen == null || Objects.equals(regexEngine.pathTaken(), "")) {
            //todo: proper exception
            throw new RuntimeException("Improper token met: " + regexEngine.pathTaken() + " at " + pos);
        }
        return new Token(lastSeen.apply(regexEngine.pathTaken()), pos);
    }

    public static void main(String[] args) {
        try (InputStream target = Files.newInputStream(Path.of(args[0]))) {
            var stream = new IteratorSingleIterableAdapter<>(new TokenStream(target));
            var source = StreamSupport
                    .stream(stream.spliterator(), false)
                    .filter((t) -> !t.isWhitespace()).toList();
            ArrayList<Pair<Token, String>> toPrint = new ArrayList<>();
            for (var token : source) {
                toPrint.add(new Pair<>(token, " at " + token.position()));
            }
            printAsTable(toPrint);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static<T1, T2> void printAsTable(Iterable<Pair<T1, T2>> coll){
        int maxLen = 0;
        ArrayList<Pair<String, String>> mapped = new ArrayList<>();
        for (var p: coll){
            mapped.add(new Pair<>(p.o1.toString(), p.o2.toString()));
            maxLen = Math.max(maxLen, mapped.getLast().o1.length());
        }
        for (var p: mapped){
            String tabs = '\t' + "\t".repeat(Math.max(0, (maxLen - p.o1.length()) / 4));
            System.out.println(p.o1 + tabs + p.o2);
        }
    }
}
