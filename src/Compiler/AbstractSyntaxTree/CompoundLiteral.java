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
}
