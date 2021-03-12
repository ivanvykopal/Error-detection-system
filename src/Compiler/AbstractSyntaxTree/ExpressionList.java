package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

import java.util.ArrayList;

public class ExpressionList extends Node {
    ArrayList<Node> expressions;

    public ExpressionList(ArrayList<Node> exprs, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expressions = exprs;
        setLine(line);

        for (Node node : expressions) {
            SymbolTableFiller.resolveUsage(node, table, errorDatabase, true);
        }
    }

    public void resolveUsage(SymbolTable table, int line) {
        for (Node node : expressions) {
            SymbolTableFiller.resolveUsage(node, table, line);
            node.resolveUsage(table, line);
        }
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ExpressionList: ");
        if (expressions != null) {
            for (Node expr : expressions) {
                expr.traverse(indent + "    ");
            }
        }
    }

}
