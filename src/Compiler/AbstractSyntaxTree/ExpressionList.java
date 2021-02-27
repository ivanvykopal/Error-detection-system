package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class ExpressionList extends Node {
    ArrayList<Node> expressions;

    public ExpressionList(ArrayList<Node> exprs) {
        this.expressions = exprs;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ExpressionList: ");
        if (expressions != null) {
            for (Node expr : expressions) {
                expr.traverse(indent + "    ");
            }
        }
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
