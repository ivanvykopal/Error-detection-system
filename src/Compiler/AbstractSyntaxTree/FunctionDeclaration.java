package Compiler.AbstractSyntaxTree;

public class FunctionDeclaration extends Node {
    Node arguments;
    Node type;

    public FunctionDeclaration(Node args, Node type) {
        this.arguments = args;
        this.type = type;
    }

    @Override
    public void traverse() {
        type.traverse();
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
        return type;
    }

    @Override
    public void addType(Node type) {
        this.type = type;
    }
}
