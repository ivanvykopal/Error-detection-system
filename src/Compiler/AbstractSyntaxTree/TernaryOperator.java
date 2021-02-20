package Compiler.AbstractSyntaxTree;

public class TernaryOperator extends Node {
    Node condition;
    Node truePart;
    Node falsePart;

    public TernaryOperator(Node cond, Node truePart, Node falsePart) {
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
}
