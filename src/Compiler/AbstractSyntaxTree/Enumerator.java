package Compiler.AbstractSyntaxTree;

public class Enumerator extends Node {
    String name;
    Node value;

    public Enumerator(String name, Node value) {
         this.name = name;
         this.value = value;
    }

    @Override
    public void traverse() {
        //System.out.println(name);
        value.traverse();
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
