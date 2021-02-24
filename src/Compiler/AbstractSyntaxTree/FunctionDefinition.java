package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class FunctionDefinition extends Node {
    Node declaration;
    ArrayList<Node> parameters;
    Node body;

    public FunctionDefinition(Node decl, ArrayList<Node> params, Node body) {
        this.declaration = decl;
        this.parameters = params;
        this.body = body;
    }

    @Override
    public void traverse() {
        declaration.traverse();
        for (Node param : parameters) {
            param.traverse();
        }
        body.traverse();
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
}
