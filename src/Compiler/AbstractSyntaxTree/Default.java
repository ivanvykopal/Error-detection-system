package Compiler.AbstractSyntaxTree;

public class Default extends Node {
    Node statement;

    public Default(Node stmt, int line) {
        this.statement = stmt;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Default: ");
        if (statement != null) statement.traverse(indent + "    ");
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

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}
