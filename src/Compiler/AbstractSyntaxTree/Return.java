package Compiler.AbstractSyntaxTree;

public class Return extends Node {
    Node expression;

    public Return(Node expr) {
        this.expression = expr;
    }

    @Override
    public void traverse() {
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
        return null;
    }

    @Override
    public void addType(Node type) {

    }
}
