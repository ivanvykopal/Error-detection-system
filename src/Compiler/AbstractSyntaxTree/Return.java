package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

public class Return extends Node {
    Node expression;

    public Return(Node expr, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expression = expr;
        setLine(line);

        SymbolTableFiller.resolveUsage(expression, table, errorDatabase);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Return:");
        if (expression != null) expression.traverse(indent + "    ");
    }

}
