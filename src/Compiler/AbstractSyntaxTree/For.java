package Compiler.AbstractSyntaxTree;

public class For extends Node {
    Node initializer;
    Node condition;
    Node next;
    Node statement;

    public For(Node init, Node cond, Node next, Node stmt) {
        this.initializer = init;
        this.condition = cond;
        this.next = next;
        this.statement = stmt;
    }

    @Override
    public void traverse() {
        initializer.traverse();
        condition.traverse();
        next.traverse();
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
