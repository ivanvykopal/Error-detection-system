package Compiler.AbstractSyntaxTree;

public class DoWhile extends Node {
    Node condition;
    Node statement;

    public DoWhile(Node cond, Node stmt) {
        this.condition = cond;
        this.statement = stmt;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "DoWhile: ");
        if (condition != null) condition.traverse(indent + "    ");
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
