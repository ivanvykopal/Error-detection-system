package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

import java.util.ArrayList;

public class NamedInitializer extends Node {
    ArrayList<Node> names;
    Node expression;

    public NamedInitializer(ArrayList<Node> names, Node expr, SymbolTable table, ErrorDatabase errorDatabase) {
        this.names = names;
        this.expression = expr;

        SymbolTableFiller.resolveUsage(expression, table, errorDatabase);
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

}
