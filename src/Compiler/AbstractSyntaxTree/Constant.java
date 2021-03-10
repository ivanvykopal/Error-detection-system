package Compiler.AbstractSyntaxTree;

public class Constant extends Node {
    String type;
    String value;

    public Constant(String type, String value, int line) {
        this.type = type;
        this.value = value;
        setLine(line);
    }

    public String getTypeSpecifier() {
        return type;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Constant: ");
        if (type != null) System.out.println(indent + type);
        if (value != null) System.out.println(indent + value);
    }

    public String getValue() {
        return value;
    }

}
