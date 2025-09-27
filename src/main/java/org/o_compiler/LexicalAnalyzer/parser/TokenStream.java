package org.o_compiler.LexicalAnalyzer.parser;

import org.o_compiler.LexicalAnalyzer.parser.FSM.FSM;
import org.o_compiler.LexicalAnalyzer.parser.FSM.NDFSM;
import org.o_compiler.LexicalAnalyzer.parser.FSM.TraverseIterator;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.IdentifierDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.LiteralType;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public class TokenStream implements Iterator<Token>, Iterable<Token> {
    private final CharStream source;
    private final FSM<Function<String, TokenValue>> machine;
    private int line, pos;

    public TokenStream(final InputStream target) {
        try {
            source = new CharStream(target);
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
        return source.hasNext();
    }

    @Override
    public Token next() {
        var smth = machine.traverse();
        Function<String,TokenValue> lastSeen = smth.result();
        for (char c: source){
            smth.feed(c);
            if (c=='\n'){
                line++;
                pos = 0;
            } else if (c == '\t') {
                pos += 3;
            }
            if (smth.isEnd()){
                source.revert();
                return extract(lastSeen, smth);
            }
            pos++;
            lastSeen = smth.result();
        }
        return extract(lastSeen, smth);
    }

    @Override
    public Iterator<Token> iterator() {
        return this;
    }

    private Token extract(
            Function<String,TokenValue> lastSeen,
            TraverseIterator<Function<String,TokenValue>> smth){
        if (lastSeen == null || Objects.equals(smth.pathTaken(), "")){
            //todo: proper exception
            throw new RuntimeException("Improper token met: " + smth.pathTaken() + " at " + line + ":" + pos);
        }
        return new Token(lastSeen.apply(smth.pathTaken()), line, pos);
    }
}
