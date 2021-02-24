package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Typename extends Node {
    String name;
    ArrayList<String> qualifiers;
    Node type;

    public Typename(String name, ArrayList<String> quals, Node type) {
        this.name = name;
        this.qualifiers = quals;
        this.type = type;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
        //qualifiers -> sout
        type.traverse();
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
