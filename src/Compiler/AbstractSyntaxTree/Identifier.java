package Compiler.AbstractSyntaxTree;

public class Identifier extends Node {
    String name;

    public Identifier(String name, int line) {
        this.name = name;
        setLine(line);
    }

    public String getName() {
        return name;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Identifier: ");
        if (name != null)System.out.println(indent + name);
    }

}
