package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class AST extends Node {
    ArrayList<Node> exts;

    public AST(ArrayList<Node> exts) {
        this.exts = exts;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "AST: ");
        if (exts != null) {
            for (Node ext : exts) {
                ext.traverse(indent + "    ");
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
