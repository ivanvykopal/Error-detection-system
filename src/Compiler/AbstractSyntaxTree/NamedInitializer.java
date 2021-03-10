package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;

import java.util.ArrayList;

public class NamedInitializer extends Node {
    ArrayList<Node> names;
    Node expression;

    public NamedInitializer(ArrayList<Node> names, Node expr, SymbolTable table, ErrorDatabase errorDatabase) {
        this.names = names;
        this.expression = expr;

        resolveUsage(expression, table, errorDatabase);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "NamedInitializer: ");
        if (names != null) {
            for (Node name : names) {
                name.traverse(indent + "    ");
            }
        }
        if (expression != null) expression.traverse(indent + "    ");
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
