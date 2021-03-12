package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

public class For extends Node {
    Node initializer;
    Node condition;
    Node next;
    Node statement;

    public For(Node init, Node cond, Node next, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.initializer = init;
        this.condition = cond;
        this.next = next;
        this.statement = stmt;
        setLine(line);

        SymbolTableFiller.resolveUsage(initializer, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(next, table, errorDatabase, true);
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(initializer, table, line);
        initializer.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(condition, table, line);
        condition.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(next, table, line);
        next.resolveUsage(table, line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "For: ");
        if (initializer != null) initializer.traverse(indent + "    ");
        if (condition != null) condition.traverse(indent + "    ");
        if (next != null) next.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
