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
}
