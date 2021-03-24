package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre deklaráciu v štruktúre.
 *
 * @author Ivan Vykopal
 *
 * @see Declarator
 * @see Node
 */
public final class StructDeclarator extends Declarator {
    /** Atribút bitsize predstavuje vrchol pre počet bitov. **/
    Node bitsize;

    /**
     *
     * Konštruktor, ktorý vytvára triedu {@code StructDeclarator} a inicilizuje jej atribúty.
     *
     * @param decl vrchol pre deklarátor
     *
     * @param bitsize vrchol pre počet bitov
     */
    public StructDeclarator(Node decl, Node bitsize) {
        super(decl, null);
        this.bitsize = bitsize;
    }

    /**
     * Metóda na zistenie vrcholu pre počet bitov.
     *
     * @return vrchol pre počet bitov.
     */
    @Override
    public Node getBitsize() {
        return bitsize;
    }

}
