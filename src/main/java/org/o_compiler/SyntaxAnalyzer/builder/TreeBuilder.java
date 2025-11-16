package org.o_compiler.SyntaxAnalyzer.builder;

import org.o_compiler.CodeGeneration.BuildTreeVisitor;
import org.o_compiler.SyntaxAnalyzer.builder.Classes.ClassTreeBuilder;

import java.util.Collection;

// todo: follow ISP
public abstract class TreeBuilder {
    protected TreeBuilder parent;

    protected TreeBuilder(TreeBuilder parent) {
        this.parent = parent;
    }

    abstract public void build();

    // mixin method, that allows to find corresponding to name program entity in the context encapsulation structure
    protected abstract StringBuilder appendTo(StringBuilder to, int depth);

    protected abstract void visitSingly(BuildTreeVisitor v);

    public abstract Collection<? extends TreeBuilder> children();

    // should be overridden by context objects. Default implementation for not-context nodes
    public boolean encloseName(String name) {
        return false;
    }

    // should be overridden by context objects. Default implementation for not-context nodes
    public TreeBuilder getEnclosedName(String name) {
        return null;
    }

    protected StringBuilder appendTo(StringBuilder to, int depth, String header) {
        int newDepth;
        if (!header.isEmpty()) {
            to.append("\t".repeat(Math.max(0, depth))).append(header).append('\n');
            newDepth = depth + 1;
        } else {
            newDepth = depth;
        }
        for (var child : children()) {
            child.appendTo(to, newDepth);
        }
        return to;
    }

    public final TreeBuilder getParent() {
        return parent;
    }

    protected final ClassTreeBuilder getClass(String name) {
        TreeBuilder current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return (ClassTreeBuilder) current.getEnclosedName(name);
    }

    public final TreeBuilder findNameAbove(String name) {
        TreeBuilder current = this;
        while (current != null) {
            if (current.encloseName(name)) {
                return current.getEnclosedName(name);
            }
            current = current.getParent();
        }
        return null;
    }

    public final String toString_() {
        return appendTo(new StringBuilder(), 0).toString();
    }

    public final void visit(BuildTreeVisitor v) {
        visitSingly(v);
        for (var child : children()) {
            child.visit(v);
        }
    }
}
