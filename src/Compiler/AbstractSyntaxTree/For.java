package Compiler.AbstractSyntaxTree;

public class For extends Node {
    Node initializer;
    Node condition;
    Node next;
    Node statement;

    public For(Node init, Node cond, Node next, Node stmt) {
        this.initializer = init;
        this.condition = cond;
        this.next = next;
        this.statement = stmt;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "For: ");
        if (initializer != null) initializer.traverse(indent + "    ");
        if (condition != null) condition.traverse(indent + "    ");
        if (next != null) next.traverse(indent + "    ");
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
