package Compiler.AbstractSyntaxTree;

public class Declarator extends Node {
    Node declarator;
    Node initializer;

    public Declarator(Node decl, Node init) {
        this.declarator = decl;
        this.initializer = init;
    }

    public Node getDeclarator() {
        return declarator;
    }

    public void addDeclarator(Node decl) {
        declarator = decl;
    }

    public Node getInitializer() {
        return initializer;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Declarator: ");
        if (declarator != null) declarator.traverse(indent + "    ");
        if (initializer != null) initializer.traverse(indent + "    ");
    }

}
