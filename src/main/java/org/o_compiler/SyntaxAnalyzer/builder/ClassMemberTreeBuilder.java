package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;

import java.util.Iterator;

public abstract class ClassMemberTreeBuilder extends TreeBuilder {
    String name;
    ClassTreeBuilder type;
    Iterator<Token> sourceCode;

    ClassMemberTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, Iterable<Token> sourceCode) {
        super(parent);
        this.name = name;
        this.type = type;
        this.sourceCode = sourceCode.iterator();
    }

    public boolean isTypeOf(ClassTreeBuilder t){
        return t.equals(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttributeTreeBuilder && ((AttributeTreeBuilder) obj).name.equals(this.name)) {
            return true;
        } else if (obj instanceof MethodTreeBuilder methodObj) {
            return methodObj.equals(this);
        }
        return false;
    }
}
