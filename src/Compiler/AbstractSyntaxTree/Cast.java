package Compiler.AbstractSyntaxTree;

public class Cast extends Node {
    Node type;
    Node expression;

    public Cast(Node type, Node expr) {
        this.type = type;
        this.expression = expr;
    }

    @Override
    public void traverse() {
        type.traverse();
        expression.traverse();
    }
}
