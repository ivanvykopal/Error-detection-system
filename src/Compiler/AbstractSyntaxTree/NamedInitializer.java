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
    public void traverse(String indent) {
        System.out.println(indent + "NamedInitializer: ");
        if (names != null) {
            for (Node name : names) {
                name.traverse(indent + "    ");
            }
        }
        if (expression != null) expression.traverse(indent + "    ");
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
