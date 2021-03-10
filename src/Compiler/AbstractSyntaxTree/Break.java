package Compiler.AbstractSyntaxTree;

public class Break extends Node {

    public Break(int line) {
        setLine(line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Break: ");
    }

}
