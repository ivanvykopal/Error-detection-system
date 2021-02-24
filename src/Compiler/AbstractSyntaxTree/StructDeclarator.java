package Compiler.AbstractSyntaxTree;

public class StructDeclarator extends Node {
    Node declaration;
    Node bitsize;

    public StructDeclarator(Node decl, Node bitsize) {
        this.declaration = decl;
        this.bitsize = bitsize;
    }

    @Override
    public void traverse() {
        declaration.traverse();
        bitsize.traverse();
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
}
