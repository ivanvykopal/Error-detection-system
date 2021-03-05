package Compiler.AbstractSyntaxTree;

public class FunctionDeclaration extends Node {
    Node arguments;
    Node type;

    public FunctionDeclaration(Node args, Node type, int line) {
        this.arguments = args;
        this.type = type;
        setLine(line);
    }

    public Node getArguments() {
        return arguments;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "FunctionDeclaration: ");
        if (type != null) type.traverse(indent + "    ");
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
        return type;
    }

    @Override
    public void addType(Node type) {
        this.type = type;
    }

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}
