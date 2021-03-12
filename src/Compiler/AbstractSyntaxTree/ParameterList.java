package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

import java.util.ArrayList;

public class ParameterList extends Node {
    ArrayList<Node> parameters;

    public ParameterList(ArrayList<Node> params, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.parameters = params;
        setLine(line);

        for (Node node: parameters) {
            SymbolTableFiller.resolveUsage(node, table, errorDatabase, true);
        }
    }

    public void resolveUsage(SymbolTable table, int line) {
        for (Node node : parameters) {
            SymbolTableFiller.resolveUsage(node, table, line);
            node.resolveUsage(table, line);
        }
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ParameterList: ");
        if (parameters != null) {
            for (Node param : parameters) {
                param.traverse(indent + "    ");
            }
        }
    }

    public void addParameter(Node param) {
        parameters.add(param);
    }

    public ArrayList<Node> getParameters() {
        return parameters;
    }

}
