package Compiler.AbstractSyntaxTree;

public class InitDeclarator extends Node {
    Node declarator;
    Node initializer;

    public InitDeclarator(Node decl, Node init) {
        this.declarator = decl;
        this.initializer = init;
    }

    @Override
    public void traverse() {
        declarator.traverse();
        initializer.traverse();
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
