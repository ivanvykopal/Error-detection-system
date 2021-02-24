package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Typedef extends Node {
    String name;
    ArrayList<String> qualifiers;
    ArrayList<String> storage;
    Node type;

    public Typedef(String name, ArrayList<String> quals, ArrayList<String> storage, Node type) {
        this.name = name;
        this.qualifiers = quals;
        this.storage = storage;
        this.type = type;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
        //qualifiers -> sout
        //storage -> sout
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
