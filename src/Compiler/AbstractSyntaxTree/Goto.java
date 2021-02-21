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

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
