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

        SymbolTableFiller.resolveUsage(initializer, table, errorDatabase);
        SymbolTableFiller.resolveUsage(condition, table, errorDatabase);
        SymbolTableFiller.resolveUsage(next, table, errorDatabase);
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
