package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class Compound extends Node {
    ArrayList<Node> statements;

    public Compound(ArrayList<Node> stmts, int line) {
        this.statements = stmts;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Compound: ");
        if (statements != null) {
            for (Node stmt : statements) {
                stmt.traverse(indent + "    ");
            }
        }
    }

}
