package Compiler.AbstractSyntaxTree;

public class ArrayReference extends Node {
    Node name;
    Node index;

    public ArrayReference(Node name, Node index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public void traverse() {
        name.traverse();
        index.traverse();
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
