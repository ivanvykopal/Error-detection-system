package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Declaration extends DeclarationNode {
    ArrayList<String> storage;
    Node initValues;
    Node bitsize;

    public Declaration(String name, ArrayList<String> quals, ArrayList<String> storage, Node type, Node init, Node bitsize, int line) {
        super(name, quals, type, line);
        this.storage = storage;
        this.initValues = init;
        this.bitsize = bitsize;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Declaration: ");
        if (name != null) System.out.print(indent + name);
        if (qualifiers != null) {
            System.out.print(indent);
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
        if (storage != null) {
            System.out.print(indent);
            for (String stor : storage) {
                System.out.print(stor + ", ");
            }
            System.out.print("\n");
        }
        if (type != null) type.traverse(indent + "    ");
        if (initValues != null) initValues.traverse(indent + "    ");
        if (bitsize != null) bitsize.traverse(indent + "    ");
    }

}