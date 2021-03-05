package Compiler.AbstractSyntaxTree;

public class Return extends Node {
    Node expression;

    public Return(Node expr, int line) {
        this.expression = expr;
        setLine(line);
    }

    //TODO: type checking, či vracia vhodný typ pre funkciu ak má expression

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Return:");
        if (expression != null) expression.traverse(indent + "    ");
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

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}
