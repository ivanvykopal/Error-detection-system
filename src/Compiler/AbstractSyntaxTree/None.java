package Compiler.AbstractSyntaxTree;

public class None extends Node {

    @Override
    public void traverse() {

    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEnumStructUnion() {
        return false;
    }
}
