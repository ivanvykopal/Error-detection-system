package Compiler.AbstractSyntaxTree;

public class Label extends Node {
    String name;
    Node statement;

    public Label(String name, Node stmt) {
        this.name = name;
        this.statement = stmt;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
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

    @Override
    public boolean isEnumStructUnion() {
        return false;
    }
}
