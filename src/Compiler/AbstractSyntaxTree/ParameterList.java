package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class ParameterList extends Node {
    ArrayList<Node> parameters;

    public ParameterList(ArrayList<Node> params) {
        this.parameters = params;
    }

    @Override
    public void traverse() {
        for (Node param : parameters) {
            param.traverse();
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

    public void addParameter(Node param) {
        parameters.add(param);
    }

}
