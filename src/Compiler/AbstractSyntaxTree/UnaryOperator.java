package Compiler.AbstractSyntaxTree;

public class UnaryOperator extends Node {
    Node expression;
    String operator;

    public UnaryOperator(Node expr, String op) {
        this.expression = expr;
        this.operator = op;
    }

    @Override
    public void traverse() {
        //System.out.println(operator);
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