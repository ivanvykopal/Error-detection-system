package Compiler.AbstractSyntaxTree;

public class Enum extends Node {
    String name;
    Node values;

    public Enum(String name, Node values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
        values.traverse();
    }
}
