package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class DeclarationNode extends Node {
    String name;
    ArrayList<String> qualifiers;
    Node type;

    public DeclarationNode(String name, ArrayList<String> quals, Node type) {
        this.name = name;
        this.qualifiers = quals;
        this.type = type;
    }

    public void addName(String name) {
        this.name = name;
    }

    public ArrayList<String> getQualifiers() {
        return qualifiers;
    }

    @Override
    public void traverse(String indent) {

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

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}
