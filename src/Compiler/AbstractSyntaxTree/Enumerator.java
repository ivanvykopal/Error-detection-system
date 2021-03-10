package Compiler.AbstractSyntaxTree;

public class Enumerator extends Node {
    String name;
    Node value;

    public Enumerator(String name, Node value, int line) {
         this.name = name;
         this.value = value;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Enumerator: ");
        if (name != null) System.out.println(indent + name);
        if (value != null) value.traverse(indent + "    ");
    }

}
