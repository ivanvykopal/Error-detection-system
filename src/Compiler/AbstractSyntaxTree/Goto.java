package Compiler.AbstractSyntaxTree;

public class Goto extends Node {
    String name;

    public Goto(String name) {
        this.name = name;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
    }
}
