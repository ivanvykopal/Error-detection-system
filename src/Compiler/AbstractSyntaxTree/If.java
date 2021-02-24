package Compiler.AbstractSyntaxTree;

public class If extends Node {
    Node condition;
    Node truePart;
    Node falsePart;

    public If(Node cond, Node truePart, Node falsePart) {
        this.condition = cond;
        this.truePart = truePart;
        this.falsePart = falsePart;
    }

    @Override
    public void traverse() {
        condition.traverse();
        truePart.traverse();
        falsePart.traverse();
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
