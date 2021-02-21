package Compiler.AbstractSyntaxTree;

public class DoWhile extends Node {
    Node condition;
    Node statement;

    public DoWhile(Node cond, Node stmt) {
        this.condition = cond;
        this.statement = stmt;
    }

    @Override
    public void traverse() {
        condition.traverse();
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
