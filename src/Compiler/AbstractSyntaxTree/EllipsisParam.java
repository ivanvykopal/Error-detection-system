package Compiler.AbstractSyntaxTree;

public class EllipsisParam extends Node {

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "EllipsisParam: ");
    }

}
