package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

public class Switch extends Node {
    Node condition;
    Node statement;

    public Switch(Node cond, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.condition = cond;
        this.statement = stmt;
        setLine(line);

        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true);
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(condition, table, line);
        condition.resolveUsage(table, line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Switch: ");
        if (condition != null) condition.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
