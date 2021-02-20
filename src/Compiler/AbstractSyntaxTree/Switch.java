package Compiler.AbstractSyntaxTree;

public class Switch extends Node {
    Node condition;
    Node statement;

    public Switch(Node cond, Node stmt) {
        this.condition = cond;
        this.statement = stmt;
    }

    @Override
    public void traverse() {
        condition.traverse();
        statement.traverse();
    }
}
