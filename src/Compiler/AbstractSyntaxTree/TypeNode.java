package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class TypeNode extends Node {
    ArrayList<Node> types;
    ArrayList<String> storage;
    ArrayList<String> qualifiers;

    public TypeNode(ArrayList<Node> types, ArrayList<String> storage, ArrayList<String> quals) {
        this.types = types;
        this.storage = storage;
        this.qualifiers = quals;
    }

    @Override
    public void addType(Node type) {
        types.add(type);
    }

    public void addStorage(String storage) {
        this.storage.add(storage);
    }

    public void addQualifier(String qualifier) {
        qualifiers.add(qualifier);
    }

    public ArrayList<Node> getTypes() {
        return types;
    }

    public ArrayList<String> getStorage() {
        return storage;
    }

    public ArrayList<String> getQualifiers() {
        return qualifiers;
    }

    public Node getType(int index) {
        return types.get(index);
    }

    public Node getLastType() {
        return types.get(types.size() - 1);
    }

    public String getStorage(int index) {
        return storage.get(index);
    }

    public String getQualifier(int index) {
        return qualifiers.get(index);
    }

    public void removeType(int index) {
        types.remove(index);
    }

    public void removeLastType() {
        types.remove(types.size() - 1);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Type: ");
        if (types != null) {
            for (Node type : types) {
                type.traverse(indent + "    ");
            }
        }
        if (storage != null) {
            System.out.print(indent);
            for (String stor : storage) {
                System.out.print(stor + ", ");
            }
            System.out.print("\n");
        }
        if (qualifiers != null) {
            System.out.print(indent);
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
    }

}
