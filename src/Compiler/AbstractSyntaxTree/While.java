package Compiler.AbstractSyntaxTree;

public class While extends Node {
    Node condition;
    Node statement;

    public While(Node cond, Node stmt) {
        this.condition = cond;
        this.statement = stmt;
    }

    @Override
    public void traverse() {
        condition.traverse();
        statement.traverse();
    }
}
