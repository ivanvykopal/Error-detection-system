package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class PointerDeclaration extends Node {
    ArrayList<String> qualifiers;
    Node type;

    public PointerDeclaration(ArrayList<String> quals, Node type) {
        this.qualifiers = quals;
        this.type = type;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "PointerDeclaration: ");
        System.out.print(indent);
        if (qualifiers != null) {
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
        if (type != null) type.traverse(indent + "    ");
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
        return type;
    }

    @Override
    public void addType(Node type) {
        this.type = type;
    }

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}
