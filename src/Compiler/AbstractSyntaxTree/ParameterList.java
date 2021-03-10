package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;

import java.util.ArrayList;

public class ParameterList extends Node {
    ArrayList<Node> parameters;

    public ParameterList(ArrayList<Node> params, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.parameters = params;
        setLine(line);

        for (Node node: parameters) {
            resolveUsage(node, table, errorDatabase);
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

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEnumStructUnion() {
        return false;
    }

    @Override
    public boolean isTypeDeclaration() {
        return false;
    }

    @Override
    public Node getType() {
        return null;
    }

    @Override
    public void addType(Node type) {

    }

    public void addParameter(Node param) {
        parameters.add(param);
    }

    public ArrayList<Node> getParameters() {
        return parameters;
    }

    @Override
    public boolean isIdentifierType() {
        return false;
    }

}
