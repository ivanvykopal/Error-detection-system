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
    public void traverse(String indent) {
        System.out.println(indent + "FunctionDefinition: ");
        if (declaration != null) declaration.traverse(indent + "    ");
        if (parameters != null) {
            for (Node param : parameters) {
                param.traverse(indent + "    ");
            }
        }
        if (body != null) body.traverse(indent + "    ");
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
