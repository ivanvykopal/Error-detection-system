package Compiler.AbstractSyntaxTree;

public class Case extends Node {
    Node constant;
    Node statement;

    public Case(Node cont, Node stmt) {
        this.constant = cont;
        this.statement = stmt;
    }

    @Override
    public void traverse() {
        constant.traverse();
        //System.out.print(":");
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
