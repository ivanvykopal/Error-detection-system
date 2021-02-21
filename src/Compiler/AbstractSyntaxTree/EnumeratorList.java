package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

public class EnumeratorList extends Node {
    ArrayList<Node> enumerators;

    public EnumeratorList(ArrayList<Node> enums) {
        this.enumerators = enums;
    }

    @Override
    public void traverse() {
        for (Node enums: enumerators) {
            enums.traverse();
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
}
