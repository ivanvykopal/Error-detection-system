package Compiler.AbstractSyntaxTree;

public class Default extends Node {
    Node statement;

    public Default(Node stmt, int line) {
        this.statement = stmt;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Default: ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
