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
}
