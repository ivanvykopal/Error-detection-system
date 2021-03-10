package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

import java.util.ArrayList;

public class InitializationList extends Node {
    ArrayList<Node> expressions;

    public InitializationList(ArrayList<Node> exprs, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expressions = exprs;
        setLine(line);

        for (Node node : expressions) {
            SymbolTableFiller.resolveUsage(node, table, errorDatabase);
        }
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "InitializationList: ");
        if (expressions != null) {
            for (Node expr : expressions) {
                expr.traverse(indent + "    ");
            }
        }
    }

    public void addExpression(Node expr) {
        expressions.add(expr);
    }

}
