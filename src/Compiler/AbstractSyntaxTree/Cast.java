package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

public class Cast extends Node {
    Node type;
    Node expression;

    public Cast(Node type, Node expr, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.type = type;
        this.expression = expr;
        setLine(line);

        SymbolTableFiller.resolveUsage(expression, table, errorDatabase, true);
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(expression, table, line);
        expression.resolveUsage(table, line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Cast:");
        if (type != null) type.traverse(indent + "    ");
        if (expression != null) expression.traverse(indent + "    ");
    }

    @Override
    public Node getType() {
        return type;
    }

    @Override
    public void addType(Node type) {
        this.type = type;
    }

}
