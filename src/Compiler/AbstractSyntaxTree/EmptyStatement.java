package Compiler.AbstractSyntaxTree;

public class EmptyStatement extends Node {

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "EmptyStatement: ");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

}
