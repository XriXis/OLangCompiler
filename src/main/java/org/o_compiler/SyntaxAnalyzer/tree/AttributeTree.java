package org.o_compiler.SyntaxAnalyzer.tree;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.w3c.dom.Node;

import java.util.stream.Stream;

public class AttributeTree extends ClassMemberTree {
    Token value;
    Iterable<Token> source_code;

    public AttributeTree(
            String attribute_name,
            ClassTree attribute_parent,
            Token attribute_type,
            Token attribute_value,
            Iterable<Token> source_code
    ) {
        super(attribute_name, attribute_parent, attribute_type, source_code);
        value = attribute_value;
    }
}
