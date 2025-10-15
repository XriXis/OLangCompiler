package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.CodeSegregator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

// todo: smth with default constructor (or require explicit one, or auto-generate default
//  ?[in case of absence of any another one])

public class ClassTreeBuilder implements BuildTree {
    String className;
    Iterator<Token> source;
    RootTreeBuilder parent;
    Token tokenInheritanceParent;
    ClassTreeBuilder classInheritanceParent = null;
    ArrayList<ClassMemberTreeBuilder> classMembers;
    ArrayList<Token> genericClasses;

    public ClassTreeBuilder(String className, Iterable<Token> source, RootTreeBuilder parent, Token inheritanceParent, ArrayList<Token> genericClasses) {
        this.className = className;
        this.source = source.iterator();
        this.parent = parent;
        this.tokenInheritanceParent = inheritanceParent;
        this.genericClasses = genericClasses;
        classMembers = new ArrayList<>();
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
            if (classMemberBuilder != null)
                classMembers.add(classMemberBuilder);
        }
    }

    //todo
    // this implementation kills the possibility of overloads, so should be changed, if such feature
    // supposed to be implemented. For such an option exposed out of the @getEnclosedName
    // To Arthur - you could think about better or more unique implementation without infinite behaviour growth
    public MethodTreeBuilder getMethodByName(String name) {
        var m = getEnclosedName(name);
        if (m instanceof MethodTreeBuilder method)
            return method;
        return null;
//        throw new RuntimeException(new ExecutionControl.NotImplementedException(""));
    }

    public ClassMemberTreeBuilder scanClassMember() {
        // get field type: var, method, this
        Token fieldType;
        do fieldType = source.next();
        while (fieldType.entry().equals(ControlSign.END_LINE) && source.hasNext());

        if (!source.hasNext()) {
            return null;
        }

        // scan field
        return switch (fieldType.entry()) {
            case Keyword.VAR -> scanAttribute();
            case Keyword.METHOD -> scanMethod();
            case Keyword.THIS -> scanConstructor();
            default -> throw new CompilerError("Unexpected field type: " + fieldType);
        };
    }

    public boolean isSubclassOf(ClassTreeBuilder another) {
        if (equals(another)) return true;
        var queuedParent = classInheritanceParent;
        while (queuedParent != null) {
            if (another.equals(queuedParent)) return true;
            queuedParent = queuedParent.classInheritanceParent;
        }
        return false;
    }

    @Override
    public boolean equals(Object another){
        // todo: think about lifecycles of class values. "==" is place for potential bug.
        //  Now, each class object is unique (i do not know how generics are implemented, so should be considered
        //  separately), but it could be not so in the future
        return another == this;
    }

    @Override
    public boolean encloseName(String name) {
        for (ClassMemberTreeBuilder classMember: classMembers) {
            if (classMember.name.equals(name))
                return true;
        }
        return false;
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
            } else if (getClass(tokenReturnType.entry().value()) == null && !enclosePolymorphicName(tokenReturnType.entry().value())) {
                throw new CompilerError("Class " + tokenReturnType.entry().value() + " not found");
            }
            returnType = getClass(tokenReturnType.entry().value());
            nextToken = source.next();
        }

        ArrayList<Token> body = new ArrayList<>();
        // multiline body
        if (nextToken.entry().equals(Keyword.IS)) {
            body = scanMultilineBody();
        } else if (nextToken.entry().equals(ControlSign.LAMBDA)) {
            body.add(new Token(Keyword.RETURN, nextToken.position()));
            body.addAll(new CodeSegregator(source).scanBracesExpr());
        } else if (!nextToken.entry().equals(ControlSign.END_LINE)) {
            throw new CompilerError("Body of method " + varName.entry().value() + " not found");
        }

        MethodTreeBuilder methodTreeBuilder = new MethodTreeBuilder(varName.entry().value(), returnType, parameters, this, body);
        if (classMembers.contains(methodTreeBuilder)) {
            throw new CompilerError("Method with name " + varName.entry().value() + " of class " + this.className + " with such parameters already exists");
        }
        return methodTreeBuilder;
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

        AttributeTreeBuilder attributeTreeBuilder = new AttributeTreeBuilder(varName.entry().value(), treeTypeOfAttribute, this, valueSourceCode);

        if (classMembers.contains(attributeTreeBuilder)) {
            throw new CompilerError("Attribute with name " + varName.entry().value() + " already exists");
        }

        return attributeTreeBuilder;
    }

    private MethodTreeBuilder scanConstructor() {
        var nextToken = source.next();
        if (!nextToken.entry().equals(ControlSign.PARENTHESIS_OPEN)) {
            throw new CompilerError("Unexpected token: " + nextToken);
        }
        ArrayList<Variable> parameters = scanParameters();
        nextToken = source.next();

        ArrayList<Token> body = new ArrayList<>();
        if (nextToken.entry().equals(Keyword.IS)) {
            body = scanMultilineBody();
        } else if (!nextToken.entry().equals(ControlSign.END_LINE)) {
            throw new CompilerError("Unexpected token: " + nextToken);
        }

        MethodTreeBuilder methodTreeBuilder = new MethodTreeBuilder("this", this, parameters, this, body);
        if (classMembers.contains(methodTreeBuilder)) {
            throw new CompilerError("The same constructor of class " + this.className + " already exists");
        }
        return methodTreeBuilder;
    }

    private ArrayList<Variable> scanParameters() {
        // todo: add method object propagation as parent of parameters
        ArrayList<Variable> parameters = new ArrayList<>();
        var nextToken = source.next();
        // type of (arg: Type)
        while (!nextToken.entry().equals(ControlSign.PARENTHESIS_CLOSED)) {
            var param = nextToken;
            if (!(param.entry() instanceof Identifier)) {
                throw new CompilerError("Unexpected parameter name: " + param + " at " + param.position());
            }
            var column = source.next();
            if (!(column.entry().equals(ControlSign.COLUMN))) {
                throw new CompilerError("Unexpected token: " + column);
            }
            var type = source.next();
            if (!(type.entry() instanceof Identifier)) {
                throw new CompilerError("Unexpected parameter type: " + type);
            } else if (getClass(type.entry().value()) == null && !enclosePolymorphicName(type.entry().value())) {
                throw new CompilerError("Class " + type.entry().value() + " not found");
            }
            var classParameterType = getClass(type.entry().value());
            nextToken = source.next();
            // check polymorphism
            Token polyClassName = null;
            if (nextToken.entry().equals(ControlSign.BRACKET_OPEN)) {
                polyClassName = source.next();
                if (getClass(polyClassName.entry().value()) == null && this.genericClasses.size() != 1) {
                    throw new CompilerError("Unknown parameter type " + polyClassName + " met at " + polyClassName.position());
                }
                nextToken = source.next();
                if (!nextToken.entry().equals(ControlSign.BRACKET_CLOSED)) {
                    throw new CompilerError("BRACKET_CLOSED expected, met " + nextToken + " at " + nextToken.position());
                }
                nextToken = source.next();
            }
            parameters.add(new Variable(param.entry().value(), classParameterType, polyClassName));

            if (!nextToken.entry().equals(ControlSign.SEPARATOR) && !nextToken.entry().equals(ControlSign.PARENTHESIS_CLOSED)) {
                throw new CompilerError("Unexpected token: " + nextToken);
            } else if (nextToken.entry().equals(ControlSign.SEPARATOR)) {
                nextToken = source.next();
            }
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

    public boolean enclosePolymorphicName(String name) {
        return genericClasses.stream()
                .anyMatch(c -> c.entry().value().contains(name));
    }

    // TODO use this
    public MethodTreeBuilder getMethod(String name, List<ClassTreeBuilder> parameters) {
        var foundMethods = classMembers.stream()
                .filter(classMember -> classMember instanceof MethodTreeBuilder)
                .map(classMember -> (MethodTreeBuilder) classMember)
                .filter(method -> method.name.equals(name) && method.parameters.size() == parameters.size())
                .filter(method -> IntStream.range(0, method.parameters.size())
                        .allMatch(i -> method.parameters.get(i).type.className.equals(parameters.get(i).className)))
                .toList();

        if (foundMethods.size() > 1) {
            throw new CompilerError("More than one method with name " + name + " and parameters " + parameters + " is found");
        }

        return foundMethods.isEmpty() ? null : foundMethods.getFirst();
    }

    @Override
    public ClassMemberTreeBuilder getEnclosedName(String name) {
        for (ClassMemberTreeBuilder classMember: classMembers) {
            if (classMember.name.equals(name))
                return classMember;
        }
        if (classInheritanceParent != null) {
            return classInheritanceParent.getEnclosedName(name);
        } else {
            return null;
        }
    }

    @Override
    public StringBuilder appendTo(StringBuilder to, int depth) {
        var children = new ArrayList<>(classMembers
                .stream()
                .filter((m) -> (m instanceof AttributeTreeBuilder))
                .toList());
        children.addAll(classMembers
                .stream()
                .filter((m) -> (m instanceof MethodTreeBuilder))
                .toList());
        return BuildTree.appendTo(to, depth, "Class " + className, children);
    }

    @Override
    public BuildTree getParent() {
        return parent;
    }

    @Override
    public void build() {
        var attrs = classMembers
                .stream()
                .filter((m) -> (m instanceof AttributeTreeBuilder))
                .toList();
        var methods = classMembers
                .stream()
                .filter((m) -> (m instanceof MethodTreeBuilder))
                .toList();
        for (var attr : attrs)
            attr.build();
        for (var method : methods)
            method.build();
    }

    @Override
    public String toString() {
        return "[O-Lang class: " + simpleName() + "]";
    }

    public String simpleName(){
        return className;
    }
}
