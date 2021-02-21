package Compiler.AbstractSyntaxTree;

public class Return extends Node {
    Node expression;

    public Return(Node expr) {
        this.expression = expr;
    }

    @Override
    public void traverse() {
        expression.traverse();
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
