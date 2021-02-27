package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class EnumeratorList extends Node {
    ArrayList<Node> enumerators;

    public EnumeratorList(ArrayList<Node> enums) {
        this.enumerators = enums;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "EnumeratorList: ");
        if (enumerators != null) {
            for (Node enums : enumerators) {
                enums.traverse(indent + "    ");
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
