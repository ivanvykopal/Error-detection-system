package Compiler.AbstractSyntaxTree;

public abstract class Node {
    protected int line;

    public Node() {

    }

    abstract public void traverse(String indent);

    abstract public boolean isNone();

    abstract public boolean isEmpty();

    abstract public boolean isEnumStructUnion();

    abstract public boolean isTypeDeclaration();

    abstract public Node getType();

    abstract public void addType(Node type);

    abstract public boolean isIdentifierType();

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

}