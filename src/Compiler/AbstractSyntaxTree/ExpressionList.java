package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class ExpressionList extends Node {
    ArrayList<Node> expressions;

    public ExpressionList(ArrayList<Node> exprs) {
        this.expressions = exprs;
    }

    @Override
    public void traverse() {
        for (Node expr : expressions) {
            expr.traverse();
        }
    }
}
