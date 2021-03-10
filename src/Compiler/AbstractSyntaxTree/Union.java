package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Union extends Node {
    String name;
    ArrayList<Node> declarations;

    public Union(String name, ArrayList<Node> decls, int line) {
        this.name = name;
        this.declarations = decls;
        setLine(line);
    }

    public String getName() {
        return name;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Union:");
        if (declarations != null) {
            for (Node decl : declarations) {
                decl.traverse(indent + "    ");
            }
        }
    }

    @Override
    public boolean isEnumStructUnion() {
        return true;
    }

}
