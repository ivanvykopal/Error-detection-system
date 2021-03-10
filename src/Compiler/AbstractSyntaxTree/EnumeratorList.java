package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class EnumeratorList extends Node {
    ArrayList<Node> enumerators;

    public EnumeratorList(ArrayList<Node> enums, int line) {
        this.enumerators = enums;
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "EnumeratorList: ");
        if (enumerators != null) {
            for (Node enums : enumerators) {
                enums.traverse(indent + "    ");
            }
        }
    }

}
