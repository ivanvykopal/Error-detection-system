package Compiler.AbstractSyntaxTree;

public class Err extends Node {
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
}