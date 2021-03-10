package Compiler.AbstractSyntaxTree;

public class Goto extends Node {
    String name;

    public Goto(String name, int line) {
        this.name = name;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Goto: ");
        if (name != null) System.out.println(indent + name);
    }

}
