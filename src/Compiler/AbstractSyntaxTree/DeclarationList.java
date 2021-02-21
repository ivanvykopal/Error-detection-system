package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class DeclarationList extends Node {
    ArrayList<Node> declarations;

    public DeclarationList ( ArrayList<Node> decls) {
        this.declarations = decls;
    }

    @Override
    public void traverse() {
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
