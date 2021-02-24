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

    @Override
    public boolean isEnumStructUnion() {
        return false;
    }

    @Override
    public boolean isTypeDeclaration() {
        return false;
    }

    @Override
    public Node getType() {
        return null;
    }

    @Override
    public void addType(Node type) {

    }
}