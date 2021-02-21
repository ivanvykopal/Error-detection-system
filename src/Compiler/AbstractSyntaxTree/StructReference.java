package Compiler.AbstractSyntaxTree;

public class StructReference extends Node {
    Node name;
    String type;
    Node field;

    public StructReference(Node name, String type, Node field) {
        this.name = name;
        this.type = type;
        this.field = field;
    }

    @Override
    public void traverse() {
        name.traverse();
        //System.out.println(type);
        field.traverse();
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
