package Compiler.AbstractSyntaxTree;

public class StructDeclarator extends Declarator {
    Node bitsize;

    public StructDeclarator(Node decl, Node bitsize) {
        super(decl, null);
        this.bitsize = bitsize;
    }

}
