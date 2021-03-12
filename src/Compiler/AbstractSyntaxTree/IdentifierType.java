package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class IdentifierType extends Node {
    ArrayList<String> names;

    public IdentifierType(ArrayList<String> name, int line) {
        this.names = name;
        setLine(line);
    }

    public String getName(int index) {
        return names.get(index);
    }

    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Identifier Type");
        if (names != null) {
            System.out.print(indent);
            for (String name : names) {
                System.out.print(name + ", ");
            }
            System.out.print("\n");
        }
    }

}
