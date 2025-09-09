package org.o_compiler.LexicalAnalyzer.parser.FSM;

import org.o_compiler.LexicalAnalyzer.tokens.value.TokenValue;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.IdentifierDescription;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.LiteralType;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.literal.Literal;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;

import java.util.HashMap;
import java.util.function.Function;

public class FabricSourceMatch {
    static final HashMap<String, Function<String, TokenValue>> configuration = new HashMap<>();
    static {
        // todo: change this explicit enumeration of the classes to "All inheritors of the TokenDescriptor"
        for (var type: LiteralType.values()){
            configuration.put(type.pattern(), Literal::new);
        }
        for (var type: ControlSign.values()){
            configuration.put(type.pattern(), str->type);
        }
        for (var type: Keyword.values()){
            configuration.put(type.pattern(), str->type);
        }
//        configuration.put(new IdentifierDescription().pattern(), Identifier::new);
    }

    public FabricSourceMatch(){
        NDFSM<Function<String, TokenValue>> machine = NDFSM.fromRegex(
                new IdentifierDescription().pattern(),
                Identifier::new
        );
        for (var pattern: configuration.keySet()){
            machine = machine.union(NDFSM.fromRegex(pattern, configuration.get(pattern)));
        }
//        machine.determinize()
    }
}
