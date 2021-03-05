package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class InitializationList extends Node {
    ArrayList<Node> expressions;

    public InitializationList(ArrayList<Node> exprs, int line) {
        this.expressions = exprs;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "InitializationList: ");
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

    public void addExpression(Node expr) {
        expressions.add(expr);
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
