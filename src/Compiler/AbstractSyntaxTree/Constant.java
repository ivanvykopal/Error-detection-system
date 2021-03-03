package Compiler.AbstractSyntaxTree;

public class Constant extends Node {
    String type;
    String value;

    public Constant(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getTypeSpecifier() {
        return type;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Constant: ");
        if (type != null) System.out.println(indent + type);
        if (value != null) System.out.println(indent + value);
    }

    public String getValue() {
        return value;
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
