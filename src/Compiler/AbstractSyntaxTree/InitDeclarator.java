package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.SymbolTable;

public class InitDeclarator extends Declarator {

    public InitDeclarator(Node decl, Node init) {
        super(decl, init);
    }

    //TODO: type checking
    private boolean typeCheck(SymbolTable table) {
        if (initializer == null || declarator == null) {
            return true;
        }



        return false;
    }
    
}
