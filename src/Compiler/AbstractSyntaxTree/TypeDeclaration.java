package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class TypeDeclaration extends Node {
    String declname;
    ArrayList<String> qualifiers;
    Node type;

    public TypeDeclaration(String declname, ArrayList<String> quals, Node type) {
        this.declname = declname;
        this.qualifiers = quals;
        this.type = type;
    }

    public String getDeclname() {
        return declname;
    }

    public void addDeclname(String declname) {
        this.declname = declname;
    }

    public void addQualifiers(ArrayList<String> quals) {
        this.qualifiers = quals;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "TypeDeclaration: ");
        if (declname != null) System.out.println(indent + declname);
        if (qualifiers != null) {
            System.out.print(indent);
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
        return true;
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
