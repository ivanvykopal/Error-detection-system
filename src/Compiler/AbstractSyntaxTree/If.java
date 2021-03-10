package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;

public class If extends Node {
    Node condition;
    Node truePart;
    Node falsePart;

    public If(Node cond, Node truePart, Node falsePart, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.condition = cond;
        this.truePart = truePart;
        this.falsePart = falsePart;
        setLine(line);

        resolveUsage(condition, table, errorDatabase);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "If: ");
        if (condition != null) condition.traverse(indent + "    ");
        if (truePart != null) truePart.traverse(indent + "    ");
        if (falsePart != null) falsePart.traverse(indent + "    ");
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
