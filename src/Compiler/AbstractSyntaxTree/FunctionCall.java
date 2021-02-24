package Compiler.AbstractSyntaxTree;

public class FunctionCall extends Node {
    Node name;
    Node arguments;

    public FunctionCall(Node name, Node args) {
        this.name = name;
        this.arguments = args;
    }

    @Override
    public void traverse() {
        name.traverse();
        arguments.traverse();
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEnumStructUnion() {
        return false;
    }

    @Override
    public boolean isTypeDeclaration() {
        return false;
    }

    @Override
    public Node getType() {
        return null;
    }

    @Override
    public void addType(Node type) {

    }
}
