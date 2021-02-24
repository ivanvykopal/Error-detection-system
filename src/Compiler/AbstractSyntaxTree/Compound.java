package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Compound extends Node {
    ArrayList<Node> statements;

    public Compound(ArrayList<Node> stmts) {
        this.statements = stmts;
    }

    @Override
    public void traverse() {
        for (Node stmt: statements) {
            stmt.traverse();
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
}
