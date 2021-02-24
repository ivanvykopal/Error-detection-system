package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Declaration extends Node {
    String name;
    ArrayList<String> qualifiers;
    ArrayList<String> storage;
    Node type;
    Node initValues;
    Node size;

    public Declaration(String name, ArrayList<String> quals, ArrayList<String> storage, Node type, Node init, Node size) {
        this.name = name;
        this.qualifiers = quals;
        this.storage = storage;
        this.type = type;
        this.initValues = init;
        this.size = size;
    }

    @Override
    public void traverse() {
        //name -> sout
        //qualifiers -> sout
        //storage -> sout
        type.traverse();
        initValues.traverse();
        size.traverse();
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
