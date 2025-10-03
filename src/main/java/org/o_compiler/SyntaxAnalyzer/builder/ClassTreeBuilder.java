package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.CompilerError;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.tree.ClassMemberTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ClassTreeBuilder implements BuildTree {
    String className;
    Iterator<Token> source;
    RootTreeBuilder parent;
    HashMap<String, ClassMemberTreeBuilder> classMembers;

    public ClassTreeBuilder(String className, Iterable<Token> source, RootTreeBuilder parent) {
        this.className = className;
        this.source = source.iterator();
        this.parent = parent;
        classMembers = new HashMap<>();
    }

    public void scanClassMembers() {
        // scan all classes members and add to HashMap
        while (source.hasNext()) {
            var classMemberBuilder = scanClassMember();
            classMembers.put(classMemberBuilder.name, classMemberBuilder);
        }
    }

    public ClassMemberTreeBuilder scanClassMember() {
        // get field type: var, method, this
        Token fieldType;
        do fieldType = source.next();
        while (fieldType.entry().equals(ControlSign.END_LINE));

        // scan field
        return switch (fieldType.entry()) {
            case Keyword.VAR -> scanAttribute();
            case Keyword.METHOD -> scanMethod();
            case Keyword.THIS -> scanConstructor();
            default -> throw new CompilerError("Unexpected field type: " + fieldType);
        };
    }

    private MethodTreeBuilder scanMethod() {
        // get variable name
        var varName = source.next();
        // if not identifier, throw error
        if (!(varName.entry() instanceof Identifier)) {
            throw new CompilerError("Unexpected variable name: " + varName);
        }
        System.out.println(varName);
        var nextToken = source.next();
        // either params, or :, or is
        ArrayList<ArrayList<Object>> parameters = new ArrayList<>();
        // params
        if (nextToken.entry().equals(ControlSign.PARENTHESIS_OPEN)) {

        }
//        // multiline, return void
//        if (nextToken.entry().equals(Keyword.IS)) {
//            // fill body
//            ArrayList<Token> body = new ArrayList<>();
//            nextToken = source.next();
//            while (!nextToken.entry().equals(Keyword.END)) {
//                body.add(nextToken);
//                nextToken = source.next();
//            }
//            // remove first and last tokens \n
//            body.removeFirst();
//            body.removeLast();
//
//            return new MethodTreeBuilder(varName.entry().value(), getClass("Void"), this, body);
//            // multiline, return type
//        } else if (nextToken.entry().equals()) {
//
//        }
        return null;
    }

    private AttributeTreeBuilder scanAttribute() {
        // get variable name
        var varName = source.next();
        // if not identifier, throw error
        if (!(varName.entry() instanceof Identifier)) {
            throw new CompilerError("Unexpected variable name: " + varName);
        }
        // skip control sign :
        if (!source.next().entry().equals(ControlSign.COLUMN)) {
            throw new CompilerError("Unexpected token: " + source.next());
        }
        // get type of attribute
        var tokenTypeOfAttribute = source.next();
        // check for identifier
        if (!(tokenTypeOfAttribute.entry() instanceof Identifier)) {
            throw new CompilerError("Unexpected variable type: " + tokenTypeOfAttribute);
        }
        // check class exists
        var treeTypeOfAttribute = getClass(tokenTypeOfAttribute.entry().value());
        if (treeTypeOfAttribute == null) {
            throw new CompilerError("Class " + tokenTypeOfAttribute.entry().value() + " not found");
        }

        // get value
        var valueSourceCode = new ArrayList<Token>();
        valueSourceCode.add(tokenTypeOfAttribute);
        var nextToken = source.next();
        // read until end of line
        while (!nextToken.entry().equals(ControlSign.END_LINE)) {
            valueSourceCode.add(nextToken);
            nextToken = source.next();
        }

        return new AttributeTreeBuilder(varName.entry().value(), treeTypeOfAttribute, this, valueSourceCode);
    }

    private MethodTreeBuilder scanConstructor() {
        return null
    }

    public boolean encloseName(String name) {
        return classMembers.containsKey(name);
    }

    @Override
    public BuildTree getParent() {
        return parent;
    }

    @Override
    public void build() {
    }

    @Override
    public boolean encloseName(String name) {
        return false;
    }
}
