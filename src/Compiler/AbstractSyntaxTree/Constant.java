package Compiler.AbstractSyntaxTree;

public class Constant extends Node {
    String type;
    String value;

    public Constant(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void traverse() {
        //System.out.println(type);
        //System.out.println(value);
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
