package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Union extends Node {
    String name;
    ArrayList<Node> declarations;

    public Union(String name, ArrayList<Node> decls) {
        this.name = name;
        this.declarations = decls;
    }

    @Override
    public void traverse() {
        for (Node decl : declarations) {
            decl.traverse();
        }
    }
}
