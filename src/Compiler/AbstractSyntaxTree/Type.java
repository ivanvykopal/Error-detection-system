package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Type extends Node {
    ArrayList<Node> types;
    ArrayList<String> storage;
    ArrayList<String> qualifiers;

    public Type(ArrayList<Node> types, ArrayList<String> storage, ArrayList<String> quals) {
        this.types = types;
        this.storage = storage;
        this.qualifiers = quals;
    }

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

    public String getStorage(int index) {
        return storage.get(index);
    }

    public String getQualifier(int index) {
        return qualifiers.get(index);
    }

    @Override
    public void traverse() {
        for (Node type : types) {
            type.traverse();
        }
        //storage -> sout
        //qualifiers -> sout
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
}
