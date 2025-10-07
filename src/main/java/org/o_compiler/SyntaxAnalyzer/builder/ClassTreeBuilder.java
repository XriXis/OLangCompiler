package org.o_compiler.SyntaxAnalyzer.builder;

import jdk.jshell.spi.ExecutionControl;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;

import javax.swing.tree.TreePath;
import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ClassTreeBuilder implements BuildTree {
    String className;
    Iterator<Token> source;
    RootTreeBuilder parent;
    Token tokenInheritanceParent;
    ClassTreeBuilder classInheritanceParent = null;
    HashMap<String, ClassMemberTreeBuilder> classMembers;

    public ClassTreeBuilder(String className, Iterable<Token> source, RootTreeBuilder parent, Token inheritanceParent) {
        this.className = className;
        this.source = source.iterator();
        this.parent = parent;
        this.tokenInheritanceParent = inheritanceParent;
        classMembers = new HashMap<>();
    }

    public void scanClassMembers() {
        if (tokenInheritanceParent != null) {
            if (getClass(tokenInheritanceParent.entry().value()) == null) {
                throw new CompilerError("Inherited class " + tokenInheritanceParent.entry().value() + " not found");
            } else {
                classInheritanceParent = getClass(tokenInheritanceParent.entry().value());
            }
        }

        // scan all classes members and add to HashMap
        while (source.hasNext()) {
            var classMemberBuilder = scanClassMember();
            classMembers.put(classMemberBuilder.name, classMemberBuilder);
        }
    }

    // this implementation kills the possibility of overloads, so should be changed, if such feature
    // supposed to be implemented. For such an option exposed out of the @getEnclosedName
    // To Arthur - you could think about better or more unique implementation without infinite behaviour growth
    public MethodTreeBuilder getMethodByName(String name) {
        throw new RuntimeException(new ExecutionControl.NotImplementedException(""));
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
        // either params, or :, or is
        var nextToken = source.next();
        ArrayList<Variable> parameters;
        // parameters
        if (nextToken.entry().equals(ControlSign.PARENTHESIS_OPEN)) {
            parameters = scanParameters();
            nextToken = source.next();
        } else {
            parameters = new ArrayList<>();
        }
        // : means return type next
        ClassTreeBuilder returnType = getClass("Void");
        if (nextToken.entry().equals(ControlSign.COLUMN)) {
            var tokenReturnType = source.next();
            if (!(tokenReturnType.entry() instanceof Identifier)) {
                throw new CompilerError("Unexpected type name: " + tokenReturnType);
            } else if (getClass(tokenReturnType.entry().value()) == null) {
                throw new CompilerError("Class " + tokenReturnType.entry().value() + " not found");
            }
            returnType = getClass(tokenReturnType.entry().value());
            nextToken = source.next();
        }

        ArrayList<Token> body;
        // multiline body
        if (nextToken.entry().equals(Keyword.IS)) {
            body = scanMultilineBody();
        } else if (nextToken.entry().equals(ControlSign.LAMBDA)) {
            nextToken = source.next();
            body = new ArrayList<>();
            while (!nextToken.entry().equals(ControlSign.END_LINE)) {
                body.add(nextToken);
                nextToken = source.next();
            }
        } else {
            throw new CompilerError("Body of method " + varName.entry().value() + " not found");
        }

        return new MethodTreeBuilder(varName.entry().value(), returnType, parameters, this, body);
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

        // read until end of line
        Token nextToken = null;
        while (source.hasNext() && (nextToken == null || !ControlSign.END_LINE.equals(nextToken.entry()))) {
            nextToken = source.next();
            valueSourceCode.add(nextToken);
        }

        return new AttributeTreeBuilder(varName.entry().value(), treeTypeOfAttribute, this, valueSourceCode);
    }

    private MethodTreeBuilder scanConstructor() {
        var nextToken = source.next();
        if (!nextToken.entry().equals(ControlSign.PARENTHESIS_OPEN)) {
            throw new CompilerError("Unexpected token: " + nextToken);
        }
        ArrayList<Variable> parameters = scanParameters();
        nextToken = source.next();
        if (!nextToken.entry().equals(Keyword.IS)) {
            throw new CompilerError("Unexpected token: " + nextToken);
        }

        ArrayList<Token> body = scanMultilineBody();
        return new MethodTreeBuilder("this", this, parameters, this, body);
    }

    private ArrayList<Variable> scanParameters() {
        ArrayList<Variable> parameters = new ArrayList<>();
        var nextToken = source.next();
        // type of (arg: Type)
        while (!nextToken.entry().equals(ControlSign.PARENTHESIS_CLOSED)) {
            var param = nextToken;
            if (!(param.entry() instanceof Identifier)) {
                throw new CompilerError("Unexpected parameter name: " + param);
            }
            var column = source.next();
            if (!(column.entry().equals(ControlSign.COLUMN))) {
                throw new CompilerError("Unexpected token: " + column);
            }
            var type = source.next();
            if (!(type.entry() instanceof Identifier)) {
                throw new CompilerError("Unexpected parameter type: " + type);
            } else if (getClass(type.entry().value()) == null) {
                throw new CompilerError("Class " + type.entry().value() + " not found");
            }

            parameters.add(new Variable(param.entry().value(), getClass(type.entry().value())));
            nextToken = source.next();
        }
        return parameters;
    }

    private ArrayList<Token> scanMultilineBody() {
        ArrayList<Token> body = new ArrayList<>();
        var curr_braces = 1;
        var nextToken = source.next();
        while (curr_braces != 0) {
            if (nextToken.entry().equals(Keyword.IS) || nextToken.entry().equals(Keyword.LOOP)) {
                curr_braces++;
            } else if (nextToken.entry().equals(Keyword.END)) {
                curr_braces--;
            }
            body.add(nextToken);
            if (!source.hasNext()) {
                throw new CompilerError("Unclosed method declaration found at " + nextToken.position());
            }
            nextToken = source.next();
        }
        body.removeLast();
        return body;
    }

    @Override
    public boolean encloseName(String name) {
        return classMembers.containsKey(name);
    }

    @Override
    public ClassMemberTreeBuilder getEnclosedName(String name) {
        if (classMembers.containsKey(name)) {
            return classMembers.get(name);
        } else if (classInheritanceParent != null) {
            return classInheritanceParent.getEnclosedName(name);
        } else {
            return null;
        }
    }

    @Override
    public BuildTree getParent() {
        return parent;
    }

    @Override
    public void build() {
    }
}
