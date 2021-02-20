package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class InitializationList extends Node {
    ArrayList<Node> expressions;

    public InitializationList(ArrayList<Node> exprs) {
        this.expressions = exprs;
    }

    @Override
    public void traverse() {
        for (Node expr : expressions) {
            expr.traverse();
        }
    }
}
