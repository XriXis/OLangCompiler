package org.o_compiler.SyntaxAnalyzer.builder.Classes;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;

import java.util.Iterator;

public abstract class ClassMemberTreeBuilder extends TreeBuilder {
    String name;
    ClassTreeBuilder type;
    Iterator<Token> sourceCode;
    ClassTreeBuilder owner;
    int pos;

    ClassMemberTreeBuilder(String name, ClassTreeBuilder type, ClassTreeBuilder parent, ClassTreeBuilder owner, Iterable<Token> sourceCode) {
        super(parent);
        this.name = name;
        this.type = type;
        this.owner = owner;
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

    public String getName() {
        return name;
    }

    public String getParentName() {
        return ((ClassTreeBuilder) parent).className;
    }

    public abstract ClassMemberTreeBuilder clone(ClassTreeBuilder owner);

    public abstract String wasmName();

    public ClassTreeBuilder getType() {
        return this.type;
    }

    public int getPos() {
        return pos;
    }
}
