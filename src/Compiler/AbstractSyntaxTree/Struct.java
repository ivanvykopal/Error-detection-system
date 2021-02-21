package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Struct extends Node {
    String name;
    ArrayList<Node> declarations;

    public Struct(String name, ArrayList<Node> decls) {
        this.name = name;
        this.declarations = decls;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
        for (Node decl : declarations) {
            decl.traverse();
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
}
