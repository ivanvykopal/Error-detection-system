package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class TypeDeclaration extends Node {
    String declname;
    ArrayList<String> qualifiers;
    Node type;

    public TypeDeclaration(String declname, ArrayList<String> quals, Node type) {
        this.declname = declname;
        this.qualifiers = quals;
        this.type = type;
    }

    @Override
    public void traverse() {
        //System.out.println(declname);
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
}
