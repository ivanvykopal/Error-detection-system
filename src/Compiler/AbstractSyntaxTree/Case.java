package Compiler.AbstractSyntaxTree;

public class Case extends Node {
    Node constant;
    Node statement;

    public Case(Node cont, Node stmt) {
        this.constant = cont;
        this.statement = stmt;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Case: ");
        if (constant != null) constant.traverse(indent + "    ");
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
