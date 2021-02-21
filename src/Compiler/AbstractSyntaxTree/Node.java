package Compiler.AbstractSyntaxTree;

public abstract class Node {

    public Node() {

    }

    abstract public void traverse();

    abstract public boolean isNone();

    abstract public boolean isEmpty();

}