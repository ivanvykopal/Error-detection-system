package Compiler.AbstractSyntaxTree;

public class ArrayReference extends Node {
    Node name;
    Node index;

    public ArrayReference(Node name, Node index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ArrayReference: ");
        if (name != null) name.traverse(indent + "    ");
        if (index != null) index.traverse(indent + "    ");
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
