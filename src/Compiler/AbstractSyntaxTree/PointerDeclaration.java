package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class PointerDeclaration extends Node {
    ArrayList<String> qualifiers;
    Node type;

    public PointerDeclaration(ArrayList<String> quals, Node type) {
        this.qualifiers = quals;
        this.type = type;
    }

    @Override
    public void traverse() {
        //qualifiers -> sout
        type.traverse();

    }
}
