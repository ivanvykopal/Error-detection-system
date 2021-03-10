package Compiler.AbstractSyntaxTree;

public class Continue extends Node {

    public Continue(int line) {
        setLine(line);
    }
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Continue: ");
    }

}
