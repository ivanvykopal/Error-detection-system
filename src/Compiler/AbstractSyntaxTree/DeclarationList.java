package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class DeclarationList extends Node {
    ArrayList<Node> declarations;

    public DeclarationList ( ArrayList<Node> decls, int line) {
        this.declarations = decls;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "DeclarationList: ");
        if (declarations != null) {
            for (Node decl : declarations) {
                decl.traverse(indent + "    ");
            }
        }
    }

}
