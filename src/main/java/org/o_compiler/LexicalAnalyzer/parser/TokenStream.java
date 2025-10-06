package org.o_compiler.LexicalAnalyzer.parser;

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
import java.util.*;
import java.util.function.Function;

public class TokenStream implements Iterator<Token> {
    private final RevertibleStream<Character> source;
    private final FSM<Function<String, TokenValue>> machine;
    private int line = 1, pos = 0;
    private char lastRead;

    public TokenStream(final InputStream target) {
        try {
            source = new RevertibleStream<>(new InputIterator(target), 5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return source.hasNext() || lastRead != '\n';
    }

    @Override
    public Token next() {
        if (!source.hasNext() && lastRead != '\n') {
            lastRead = '\n';
            return new Token(ControlSign.END_LINE, line, pos + 1);
        }
        var regexEngine = machine.traverse();
        Function<String, TokenValue> lastSeen = regexEngine.result();
        for (char c = source.next(); source.hasNext(); c = source.next()) {
            lastRead = c;
            regexEngine.feed(c);
            if (c == '\n') {
                line++;
                pos = 0;
            } else if (c == '\t') {
                pos += 3;
            }
            if (regexEngine.isEnd()) {
                source.revert();
                if (lastRead == '\n') line--;
                if (lastRead=='\t') pos-=3;
                return extract(lastSeen, regexEngine);
            }
            pos++;
            lastSeen = regexEngine.result();
        }
        return extract(lastSeen, regexEngine);
    }

    private Token extract(
            Function<String, TokenValue> lastSeen,
            TraverseIterator<Function<String, TokenValue>> regexEngine) {
        if (lastSeen == null || Objects.equals(regexEngine.pathTaken(), "")) {
            //todo: proper exception
            throw new RuntimeException("Improper token met: " + regexEngine.pathTaken() + " at " + new Span(line, pos));
        }
        return new Token(lastSeen.apply(regexEngine.pathTaken()), line, pos);
    }
}
