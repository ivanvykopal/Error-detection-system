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
    public void traverse(String indent) {
        System.out.println(indent + "BinaryOperator: ");
        if (left != null) left.traverse(indent + "    ");
        if (operator != null) System.out.println(indent + operator);
        if (right != null) right.traverse(indent + "    ");
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

    @Override
    public boolean isIdentifierType() {
        return false;
    }

}
