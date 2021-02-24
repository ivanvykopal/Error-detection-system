package Compiler.AbstractSyntaxTree;

public class CompoundLiteral extends Node {
    Node type;
    Node values;

    public CompoundLiteral(Node type, Node values) {
        this.type = type;
        this.values = values;
    }

    @Override
    public void traverse() {
        type.traverse();
        values.traverse();
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
}
