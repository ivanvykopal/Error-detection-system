package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class AST extends Node {
    ArrayList<Node> exts;

    public AST(ArrayList<Node> exts) {
        this.exts = exts;
    }

    @Override
    public void traverse() {
        for (Node ext : exts) {
            ext.traverse();
        }
    }
}
