package Compiler.AbstractSyntaxTree;

public class BinaryOperator extends Node {
    Node left;
    Node right;
    String operator;

    public BinaryOperator(Node left, String op, Node right) {
        this.left = left;
        this.operator = op;
        this.right = right;
    }

    @Override
    public void traverse() {
        left.traverse();
        //System.out.println(operator);
        right.traverse();
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
