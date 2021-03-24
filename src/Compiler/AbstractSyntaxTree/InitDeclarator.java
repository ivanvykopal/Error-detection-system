package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre "init_declarator" z gramatiky.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class InitDeclarator extends Declarator {

    /**
     * Konštruktor, ktorý vytvára triedu {@code InitDeclarator} a inicilizuje jej atribúty.
     *
     * @param decl vrchol pre inicializovanú premennú
     *
     * @param init vrchol pre inicializačnú hodnotu
     */
    public InitDeclarator(Node decl, Node init) {
        super(decl, init);
    }
    
}
