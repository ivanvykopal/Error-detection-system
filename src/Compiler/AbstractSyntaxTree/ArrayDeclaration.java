package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class ArrayDeclaration extends Node {
    Node type;
    Node dimension;
    ArrayList<String> dimensionQualifiers;

    public ArrayDeclaration(Node type, Node dim, ArrayList<String> dims, int line) {
        this.type = type;
        this.dimension = dim;
        this.dimensionQualifiers = dims;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ArrayDeclaration: ");
        if (type != null) type.traverse(indent + "    ");
        if (dimension != null) dimension.traverse(indent + "    ");
        // dimensionQualifiers -> sout
    }

    public Node getDimension() {
        return dimension;
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