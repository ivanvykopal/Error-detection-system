package Compiler.AbstractSyntaxTree;

public class Identifier extends Node {
    String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
    }
}
