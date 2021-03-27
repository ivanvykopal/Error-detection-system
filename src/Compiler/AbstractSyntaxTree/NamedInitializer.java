package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import java.util.ArrayList;
//TODO: pridať komentáre
public class NamedInitializer extends Node {
    ArrayList<Node> names;
    Node expression;

    public NamedInitializer(ArrayList<Node> names, Node expr, SymbolTable table, ErrorDatabase errorDatabase) {
        this.names = names;
        this.expression = expr;

        SymbolTableFiller.resolveUsage(expression, table, errorDatabase, true, true);
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(expression, table, line);
        expression.resolveUsage(table, line);
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
