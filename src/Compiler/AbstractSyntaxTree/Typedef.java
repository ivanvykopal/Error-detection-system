package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Typedef extends DeclarationNode {
    ArrayList<String> storage;

    public Typedef(String name, ArrayList<String> quals, ArrayList<String> storage, Node type) {
        super(name, quals, type);
        this.storage = storage;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Typedef:");
        if (name != null) System.out.println(indent + name);
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
    }
}