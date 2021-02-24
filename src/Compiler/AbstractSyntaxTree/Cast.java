package Compiler.AbstractSyntaxTree;

public class Cast extends Node {
    Node type;
    Node expression;

    public Cast(Node type, Node expr) {
        this.type = type;
        this.expression = expr;
    }

    @Override
    public void traverse() {
        type.traverse();
        expression.traverse();
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
        return type;
    }

    @Override
    public void addType(Node type) {
        this.type = type;
    }
}
