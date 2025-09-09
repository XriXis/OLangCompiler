package org.o_compiler.LexicalAnalyzer.parser;

import org.o_compiler.LexicalAnalyzer.parser.FSM.FSM;
import org.o_compiler.LexicalAnalyzer.parser.FSM.NDFSM;
import org.o_compiler.LexicalAnalyzer.parser.FSM.TraverseIterator;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.IdentifierDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.LiteralType;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;

public class TokenStream implements Iterator<Token>, Iterable<Token> {
    final CharStream source;
    final FSM<Function<String, TokenValue>> machine;
    private int line, pos;

    public TokenStream(final InputStream target) {
        try {
            source = new CharStream(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final HashMap<String, Function<String, TokenValue>> configuration = new HashMap<>();
        // todo: change this explicit enumeration of the classes to "All inheritors of the TokenDescriptor"
        for (var type : LiteralType.values()) {
            configuration.put(type.pattern(), Literal::new);
        }
        for (var type : ControlSign.values()) {
            configuration.put(type.pattern(), str -> type);
        }
        for (var type : Keyword.values()) {
            configuration.put(type.pattern(), str -> type);
        }
        configuration.put(new IdentifierDescription().pattern(), Identifier::new);
        machine = NDFSM.fromRegexes(configuration);
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public Token next() {
        var smth = machine.traverse();
//        smth.feed(source.peek());
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

    private Token extract(Function<String,TokenValue> lastSeen, TraverseIterator<Function<String,TokenValue>> smth){
        if (lastSeen == null){
            //todo: proper exception
            throw new RuntimeException("Improper token met: " + smth.pathTaken() + " at " + line + ":" + pos);
        }
        return new Token(lastSeen.apply(smth.pathTaken()), line, pos);
    }

    @Override
    public Iterator<Token> iterator() {
        return this;
    }
}
