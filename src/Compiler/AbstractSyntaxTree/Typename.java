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
}
