package Compiler.AbstractSyntaxTree;

public class IdentifierType extends Node {
    String names;

    public IdentifierType(String names) {
        this.names = names;
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
}
