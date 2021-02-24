package Compiler.AbstractSyntaxTree;

public class Continue extends Node {
    @Override
    public void traverse() {

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
}
