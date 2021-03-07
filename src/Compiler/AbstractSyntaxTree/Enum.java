package Compiler.AbstractSyntaxTree;

public class Enum extends Node {
    String name;
    Node values;

    public Enum(String name, Node values, int line) {
        this.name = name;
        this.values = values;
        setLine(line);
    }

    public String getName() {
        return name;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Enum: ");
        if (name != null) System.out.println(indent + name);
        if (values != null) values.traverse(indent + "    ");
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
        return true;
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
