package Compiler.AbstractSyntaxTree;

public class None extends Node {

    @Override
    public void traverse(String indent) {

    }

    @Override
    public boolean isNone() {
        return true;
    }

}
