package org.o_compiler.SyntaxAnalyzer.builder.Classes;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.CodeGeneration.DeferredVisitorAction;
import org.o_compiler.LexicalAnalyzer.tokens.Token;
import org.o_compiler.LexicalAnalyzer.tokens.value.client.Identifier.Identifier;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.ControlSign;
import org.o_compiler.LexicalAnalyzer.tokens.value.lang.Keyword;
import org.o_compiler.Pair;
import org.o_compiler.SyntaxAnalyzer.Exceptions.CompilerError;
import org.o_compiler.SyntaxAnalyzer.builder.EntityScanner.CodeSegregator;
import org.o_compiler.SyntaxAnalyzer.builder.TreeBuilder;
import org.o_compiler.SyntaxAnalyzer.builder.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import java.util.stream.Collectors;

// todo: smth with default constructor (or require explicit one, or auto-generate default
//  ?[in case of absence of any another one])
public class ClassTreeBuilder extends TreeBuilder {
    String className;
    Iterator<Token> source;
    Token tokenInheritanceParent;
    ClassTreeBuilder classInheritanceParent = null;
    ArrayList<ClassMemberTreeBuilder> classMembers;
    ArrayList<Token> genericClasses;
    ArrayList<Token> bufSource;
    Integer baseIndexInVTable;

    public ClassTreeBuilder(String className, Iterable<Token> source, RootTreeBuilder parent, Token inheritanceParent, ArrayList<Token> genericClasses) {
        super(parent);
        this.className = className;
        this.source = source.iterator();
        this.tokenInheritanceParent = inheritanceParent;
        this.genericClasses = genericClasses;
        classMembers = new ArrayList<>();
        bufSource = new ArrayList<>();
        for (Token token : source) {
            bufSource.add(token);
        }
    }

    // todo: make this not crutched
    public Variable getCurInstance() {
        return new Variable("this", this, this);
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

        if (((RootTreeBuilder) parent).classes.containsKey(resultClassName)) {
            return ((RootTreeBuilder) parent).classes.get(resultClassName);
        }

        // replace all appearance of generic type with given
        ArrayList<Token> resSource = new ArrayList<>();
        for (Token token : bufSource) {
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
                ((RootTreeBuilder) this.parent),
                this.tokenInheritanceParent,
                new ArrayList<>()
        );
        // add to root table
        ((RootTreeBuilder) parent).classes.put(resultClassName, implementedClass);
        if (!RootTreeBuilder.predefined.containsKey(resultClassName))
            RootTreeBuilder.predefined.put(resultClassName, implementedClass);
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
            classInheritanceParent = getClass(tokenInheritanceParent.entry().value());
            if (classInheritanceParent == null) {
                throw new CompilerError("Inherited class " + tokenInheritanceParent.entry().value() + " not found");
            } else {
                if (classInheritanceParent.tokenInheritanceParent != null &&
                        classInheritanceParent.isSubclassOf(this)
                ) {
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
                                .map(classMember -> classMember.clone(this))
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
            if (classMemberNew instanceof MethodTreeBuilder methodTreeBuilder) {
                // set overridden
                methodTreeBuilder.isOverridden = true;
                // set child on parent method
                if (classMembers.get(index) instanceof MethodTreeBuilder parentMethod) {
                    getClass(parentMethod.getParentName())
                            .getMethod(
                                    parentMethod.name,
                                    parentMethod.parameters
                                            .stream()
                                            .map(Variable::getType)
                                            .toList()
                            )
                            .overriddenMethods
                            .add(methodTreeBuilder);
                }
            }
            classMembers.set(index, classMemberNew);
        }
    }

    // todo: get rid of it
    public boolean isPredefined() {
        return RootTreeBuilder.predefined.containsKey(className);
    }

    public MethodTreeBuilder getMethod(String name, List<ClassTreeBuilder> parameters) {
        try {
            var foundMethods = classMembers.stream()
                    .filter(classMember -> classMember instanceof MethodTreeBuilder)
                    .map(classMember -> (MethodTreeBuilder) classMember)
                    .filter(method -> method.name.equals(name) && method.parameters.size() == parameters.size())
                    .filter(method -> IntStream.range(0, method.parameters.size())
                            .allMatch(i -> parameters.get(i).isSubclassOf(method.parameters.get(i).getType())))
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
            default -> throw new CompilerError("Unexpected field type: " + fieldType + " at " + fieldType.position());
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
            var type = parseType();
            returnType = type.o2.o1;
            nextToken = type.o1.getLast();
            if (nextToken.entry().equals(ControlSign.BRACKET_CLOSED))
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
        var typeOfAttribute = parseType();

        // get value
        var valueSourceCode = typeOfAttribute.o1;

        // read until end of line
        Token nextToken = null;
        while (source.hasNext() && (nextToken == null || !ControlSign.END_LINE.equals(nextToken.entry()))) {
            nextToken = source.next();
            valueSourceCode.add(nextToken);
        }

        if (isGeneric()) return null;

        return new AttributeTreeBuilder(varName.entry().value(), typeOfAttribute.o2.o1, this, valueSourceCode);
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
            // if arg type is generic (just T)
            if (encloseGenericName(type.entry().value())) {
                polyClassName = type;
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

    Pair<ArrayList<Token>, Pair<ClassTreeBuilder, ArrayList<Token>>> parseType(){
        var buff = new ArrayList<Token>();
        var tokenReturnType = source.next();
        buff.add(tokenReturnType);
        if (!(tokenReturnType.entry() instanceof Identifier)) {
            throw new CompilerError("Unexpected type name: " + tokenReturnType);
        } else if (getClass(tokenReturnType.entry().value()) == null && !encloseGenericName(tokenReturnType.entry().value())) {
            throw new CompilerError("Class " + tokenReturnType.entry().value() + " not found");
        }
        var nextToken = source.next();
        buff.add(nextToken);
        // todo: todo possible generic nesting
        var polymorphicClasses = new ArrayList<Token>();
        if (nextToken.entry().equals(ControlSign.BRACKET_OPEN)) {
            while (!nextToken.entry().equals(ControlSign.BRACKET_CLOSED)) {
                nextToken = source.next();
                buff.add(nextToken);
                if (!(nextToken.entry() instanceof Identifier)) {
                    throw new CompilerError("Class name expected at " + nextToken.position() + ", got " + nextToken);
                } else if (encloseName(nextToken.entry().value()) || polymorphicClasses.contains(nextToken)) {
                    throw new CompilerError("Class name " + nextToken.entry().value() + " already exists");
                }
                polymorphicClasses.add(nextToken);
                nextToken = source.next();
                buff.add(nextToken);
                if (nextToken.entry().equals(ControlSign.SEPARATOR)) {
                    nextToken = source.next();
                    buff.add(nextToken);
                } else if (!nextToken.entry().equals(ControlSign.BRACKET_CLOSED)) {
                    throw new CompilerError("Unexpected token met " + nextToken + " at " + nextToken.position());
                }
            }
        }
        var returnType = getClass(tokenReturnType.entry().value());
        if (!polymorphicClasses.isEmpty() && genericClasses.stream().noneMatch(polymorphicClasses::contains))
            returnType.initGenericClass(polymorphicClasses);
        return new Pair<>(buff , new Pair<>(returnType, polymorphicClasses));
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
        if (classInheritanceParent == null) {
            return appendTo(to, depth, "Class " + className);
        } else {
            return appendTo(to, depth, "Class " + className + " child of " + classInheritanceParent.simpleName());
        }
    }

    @Override
    protected DeferredVisitorAction visitSingly(BuildTreeVisitor v) {
        return v.visitClass(this, className);
    }

    // todo: optimize to ordering be used only when required, or be predefined in this way
    @Override
    public Collection<? extends TreeBuilder> children() {
        var children = new ArrayList<>(classMembers
                .stream()
                .filter((m) -> (m instanceof AttributeTreeBuilder))
                .toList());
        for (int i = 0; i < children.size(); i++) {
            children.get(i).pos = i;
        }

        children.addAll(classMembers
                .stream()
                .filter((m) -> (m instanceof MethodTreeBuilder))
                .toList());

        return children;
    }

    public boolean isGeneric() {
        return !genericClasses.isEmpty();
    }

    @Override
    public void build() {
        if (isGeneric()) return;

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

    public Integer getBaseIndexInVTable() {
        return baseIndexInVTable;
    }
}
