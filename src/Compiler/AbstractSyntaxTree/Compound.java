package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Compound extends Node {
    ArrayList<Node> statements;

    public Compound(ArrayList<Node> stmts) {
        this.statements = stmts;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Compound: ");
        if (statements != null) {
            for (Node stmt : statements) {
                stmt.traverse(indent + "    ");
            }
        }
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
