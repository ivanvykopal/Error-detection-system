package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

//TODO: Pozrieť sa na toto!!
public class NamedInitializer extends Node {
    ArrayList<Node> names;
    Node expression;

    public NamedInitializer(ArrayList<Node> names, Node expr) {
        this.names = names;
        this.expression = expr;
    }

    @Override
    public void traverse() {
        for (Node name : names) {
            name.traverse();
        }
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
