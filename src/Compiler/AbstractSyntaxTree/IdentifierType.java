package Compiler.AbstractSyntaxTree;

public class IdentifierType extends Node {
    String name;

    public IdentifierType(String name) {
        this.name = name;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
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
