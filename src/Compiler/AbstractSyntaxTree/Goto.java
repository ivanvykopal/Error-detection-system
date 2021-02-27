package Compiler.AbstractSyntaxTree;

public class Goto extends Node {
    String name;

    public Goto(String name) {
        this.name = name;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Goto: ");
        if (name != null) System.out.println(indent + name);
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
