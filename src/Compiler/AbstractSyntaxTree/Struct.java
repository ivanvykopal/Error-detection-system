package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Struct extends Node {
    String name;
    ArrayList<Node> declarations;

    public Struct(String name, ArrayList<Node> decls, int line) {
        this.name = name;
        this.declarations = decls;
        setLine(line);
    }

    public String getName() {
        return name;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Struct: ");
        if (name != null) System.out.println(indent + name);
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
