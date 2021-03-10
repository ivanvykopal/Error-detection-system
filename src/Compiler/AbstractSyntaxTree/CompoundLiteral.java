package Compiler.AbstractSyntaxTree;

public class CompoundLiteral extends Node {
    Node type;
    Node values;

    public CompoundLiteral(Node type, Node values) {
        this.type = type;
        this.values = values;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "CompoundLiteral: ");
        if (type != null) type.traverse(indent + "    ");
        if (values != null) values.traverse(indent + "    ");
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
