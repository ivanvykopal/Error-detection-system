package Compiler.AbstractSyntaxTree;

public abstract class Node {

    public Node() {

    }

    abstract public void traverse();

    abstract public boolean isNone();

    abstract public boolean isEmpty();

    abstract public boolean isEnumStructUnion();

    abstract public boolean isTypeDeclaration();

    abstract public Node getType();

    abstract public void addType(Node type);

}