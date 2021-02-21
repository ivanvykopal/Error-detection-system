package Compiler.AbstractSyntaxTree;

public class EmptyStatement extends Node {

    @Override
    public void traverse() {

    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
