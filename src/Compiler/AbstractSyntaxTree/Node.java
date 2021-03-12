package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

public abstract class Node {
    protected int line;

    public Node() {

    }

    abstract public void traverse(String indent);

    public boolean isNone() {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isEnumStructUnion() {
        return false;
    }

    public Node getType() {
        return null;
    }

    public Node getNameNode() {
        return null;
    };

    public void addType(Node type) {

    }

    public void resolveUsage(SymbolTable table, int line) {

    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

}