package Compiler.AbstractSyntaxTree;

public class FunctionCall extends Node {
    Node name;
    Node arguments;

    public FunctionCall(Node name, Node args) {
        this.name = name;
        this.arguments = args;
    }

    public Node getName() {
        return name;
    }

    //TODO: type checking pre argumenty

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "FunctionCall: ");
        if (name != null) name.traverse(indent + "    ");
        if (arguments != null) arguments.traverse(indent + "    ");
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

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}
