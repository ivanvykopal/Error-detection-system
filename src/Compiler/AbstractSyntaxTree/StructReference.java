package Compiler.AbstractSyntaxTree;

public class StructReference extends Node {
    Node name;
    String type;
    Node field;

    public StructReference(Node name, String type, Node field, int line) {
        this.name = name;
        this.type = type;
        this.field = field;
        setLine(line);
    }

    public Node getNameNode() {
        return field;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "StructReference: ");
        if (name != null) name.traverse(indent + "    ");
        if (type != null) System.out.println(indent + type);
        if (field != null) field.traverse(indent + "    ");
    }

}
