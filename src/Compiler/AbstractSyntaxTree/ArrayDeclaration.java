package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class ArrayDeclaration extends Node {
    Node type;
    Node dimension;
    ArrayList<String> dimensionQualifiers;

    public ArrayDeclaration(Node type, Node dim, ArrayList<String> dims) {
        this.type = type;
        this.dimension = dim;
        this.dimensionQualifiers = dims;
    }

    @Override
    public void traverse() {
        type.traverse();
        dimension.traverse();
        // dimensionQualifiers -> sout
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
        return type;
    }

    @Override
    public void addType(Node type) {
        this.type = type;
    }
}