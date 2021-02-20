package Compiler.AbstractSyntaxTree;

public class Assignment extends Node {
    Node left;
    Node right;
    String operator;

    public Assignment(Node left, String op, Node right) {
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
}
