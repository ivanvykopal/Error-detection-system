package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Typename extends DeclarationNode {

    public Typename(String name, ArrayList<String> quals, Node type) {
        super(name, quals, type);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Typename: ");
        if (name != null) System.out.println(indent + name);
        if (qualifiers != null) {
            System.out.print(indent);
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
        if (type != null) type.traverse(indent + "    ");
    }
}
