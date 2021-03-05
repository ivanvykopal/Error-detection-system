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

    @Override
    public boolean isTypeDeclaration() {
        return false;
    }

    @Override
    public Node getType() {
        return null;
    }

    @Override
    public void addType(Node type) {

    }

    @Override
    public boolean isIdentifierType() {
        return false;
    }


}
