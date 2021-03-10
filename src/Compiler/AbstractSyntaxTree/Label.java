package Compiler.AbstractSyntaxTree;

public class Label extends Node {
    String name;
    Node statement;

    public Label(String name, Node stmt, int line) {
        this.name = name;
        this.statement = stmt;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Label: ");
        if (name != null) System.out.println(indent + name);
        if (statement != null) statement.traverse(indent + "    ");
    }

}
