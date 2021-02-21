package Compiler.AbstractSyntaxTree;

public class Default extends Node {
    Node statement;

    public Default(Node stmt) {
        this.statement = stmt;
    }

    @Override
    public void traverse() {
        statement.traverse();
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
