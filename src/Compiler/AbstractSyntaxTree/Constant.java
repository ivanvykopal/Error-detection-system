package Compiler.AbstractSyntaxTree;

public class Constant extends Node {
    String type;
    String value;

    public Constant(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void traverse() {
        //System.out.println(type);
        //System.out.println(value);
    }
}
