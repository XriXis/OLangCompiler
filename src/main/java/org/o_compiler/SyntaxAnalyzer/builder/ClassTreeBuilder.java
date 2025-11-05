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

import java.util.stream.Collectors;

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
    ArrayList<Token> bufSource;

    public ClassTreeBuilder(String className, Iterable<Token> source, RootTreeBuilder parent, Token inheritanceParent, ArrayList<Token> genericClasses) {
        this.className = className;
        this.source = source.iterator();
        this.parent = parent;
        this.tokenInheritanceParent = inheritanceParent;
        this.genericClasses = genericClasses;
        classMembers = new ArrayList<>();
        bufSource = new ArrayList<>();
        for (Token token: source) {
            bufSource.add(token);
        }
    }

    public ClassTreeBuilder initGenericClass(ArrayList<Token> genericIdentifiers) {
        if (genericClasses.isEmpty()) {
            throw new CompilerError("Try to init generic class " + this.className + " that is not generic");
        } else if (genericIdentifiers.size() != genericClasses.size()) {
            throw new CompilerError("Size of generic identifiers does not match number of identifiers for class " + this.className + "\n Expected: " + this.genericClasses.size() + ", Got : " + genericIdentifiers.size());
        }

        // check if generated class already exists
        var resultClassName = className + "_" + genericIdentifiers
                .stream()
                .map(item -> item.entry().value())
                .collect(Collectors.joining("_"));

        if (parent.classes.containsKey(resultClassName)) {
            return parent.classes.get(resultClassName);
        }

        // replace all appearance of generic type with given
        ArrayList<Token> resSource = new ArrayList<>();
        for (Token token: bufSource) {
            if (token.entry() instanceof Identifier) {
                var indexOpt = IntStream.range(0, genericClasses.size())
                        .filter(i -> genericClasses.get(i).entry().value().equals(token.entry().value()))
                        .findFirst();
                if (indexOpt.isPresent()) {
                    var index = indexOpt.getAsInt();
                    resSource.add(genericIdentifiers.get(index));
                } else {
                    resSource.add(token);
                }
            } else {
                resSource.add(token);
            }
        }
        // create class
        var implementedClass = new ClassTreeBuilder(
                resultClassName,
                resSource,
                this.parent,
                this.tokenInheritanceParent,
                new ArrayList<>()
        );
        // add to root table
        parent.classes.put(resultClassName, implementedClass);
        parent.predefined.add(resultClassName);
        // build
        implementedClass.scanClassMembers();
        implementedClass.build();
        return implementedClass;
    }

    public void scanClassMembers() {
        if (!classMembers.isEmpty()) {
            return;
        }

        if (tokenInheritanceParent != null) {
            if (getClass(tokenInheritanceParent.entry().value()) == null) {
                throw new CompilerError("Inherited class " + tokenInheritanceParent.entry().value() + " not found");
            } else {
                classInheritanceParent = getClass(tokenInheritanceParent.entry().value());
                // check cross-inheritance
                if (classInheritanceParent.tokenInheritanceParent != null &&
                        classInheritanceParent.tokenInheritanceParent.entry().value().equals(className)) {
                    throw new CompilerError("Cross-inheritance detected for class " + className);
                }
                // build all class members of parent
                if (classInheritanceParent.classMembers.isEmpty()) {
                    classInheritanceParent.scanClassMembers();
                    classInheritanceParent.build();
                }
                // inherit all class members
                classMembers.addAll(
                        classInheritanceParent.classMembers
                                .stream()
                                .filter(classMember -> !classMember.name.equals("this"))
                                .toList()
                );
            }
        }

        // scan all classes members and add to HashMap
        while (source.hasNext()) {
            var classMemberBuilder = scanClassMember();
            addToClassMembers(classMemberBuilder);
        }

//        for (var cl: classMembers) {
//            System.out.println(cl);
//        }

        // check constructor exists
        if (classMembers.stream().noneMatch(classMember -> classMember.name.equals("this"))) {
            throw new CompilerError("Constructor for class " + className + " is not defined");
        }
    }

    private void addToClassMembers(ClassMemberTreeBuilder classMemberNew) {
        if (classMemberNew == null) {
            return;
        }

        int index = classMembers.indexOf(classMemberNew);
        if (index == -1) {
            classMembers.add(classMemberNew);
        } else if (classMembers.get(index).getParent() == this) {
            throw new CompilerError("Class member with name " + classMemberNew.name + " already exists");
        } else {
            classMembers.set(index, classMemberNew);
        }
    }

    // todo: get rid of it
    public boolean isPredefined(){
        return parent.predefined.contains(className);
    }

    public MethodTreeBuilder getMethodByName(String name) {
        var m = getEnclosedName(name);
        if (m instanceof MethodTreeBuilder method)
            return method;
        return null;
//        throw new RuntimeException(new ExecutionControl.NotImplementedException(""));
    }

    public MethodTreeBuilder getMethod(String name, List<ClassTreeBuilder> parameters) {
        try {
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

        } catch (NullPointerException exception) {
            throw new CompilerError("Method of class " + className + " with name " + name + " and parameters " + parameters + " not found");
        }
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
    public boolean equals(Object another) {
        if (another instanceof ClassTreeBuilder classTreeBuilder) {
            return classTreeBuilder.className.equals(this.className);
        } else {
            return false;
//            throw new InternalCommunicationError("Try to compare different types: " + another + " and class " + this.className);
        }
    }

    @Override
    public boolean encloseName(String name) {
        for (ClassMemberTreeBuilder classMember : classMembers) {
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
            } else if (getClass(tokenReturnType.entry().value()) == null && !encloseGenericName(tokenReturnType.entry().value())) {
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

        ArrayList<Token> body = new ArrayList<>();
        if (nextToken.entry().equals(Keyword.IS)) {
            body = scanMultilineBody();
        } else if (!nextToken.entry().equals(ControlSign.END_LINE)) {
            throw new CompilerError("Unexpected token: " + nextToken);
        }

        return new MethodTreeBuilder("this", this, parameters, this, body);
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
            } else if (getClass(type.entry().value()) == null && !encloseGenericName(type.entry().value())) {
                throw new CompilerError("Class " + type.entry().value() + " not found");
            }
            var classParameterType = getClass(type.entry().value());
            nextToken = source.next();
            // check generic type
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
            if (nextToken.entry() instanceof Keyword c && c.isBlockOpen()) {
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

    public boolean encloseGenericName(String name) {
        return genericClasses.stream()
                .anyMatch(c -> c.entry().value().contains(name));
    }

    @Override
    public ClassMemberTreeBuilder getEnclosedName(String name) {
        for (ClassMemberTreeBuilder classMember : classMembers) {
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
        if (classInheritanceParent == null) {
            return BuildTree.appendTo(to, depth, "Class " + className, children);
        } else {
            return BuildTree.appendTo(to, depth, "Class " + className + " child of " + classInheritanceParent.simpleName(), children);
        }
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

    public String simpleName() {
        return className;
    }

    @Override
    public BuildTree findNameAbove(String name) {
        return BuildTree.super.findNameAbove(name);
    }
}
