package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

public class Case extends Node {
    Node constant;
    Node statement;

    public Case(Node cont, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.constant = cont;
        this.statement = stmt;
        setLine(line);

        SymbolTableFiller.resolveUsage(constant, table, errorDatabase);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Case: ");
        if (constant != null) constant.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
