package Compiler.AbstractSyntaxTree;

public class Err extends Node {
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Err:");
    }

}