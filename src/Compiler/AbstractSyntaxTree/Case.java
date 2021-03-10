package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;

public class Case extends Node {
    Node constant;
    Node statement;

    public Case(Node cont, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.constant = cont;
        this.statement = stmt;
        setLine(line);

        resolveUsage(constant, table, errorDatabase);
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
