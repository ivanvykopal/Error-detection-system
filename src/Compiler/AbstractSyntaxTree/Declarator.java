package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca rodičovskú triedu pre {@code InitDeclarator} a pre {@code StructDeclarator}.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public class Declarator extends Node {
    /** Atribút declarator predstavuje vrchol pre ľavú časť inicializácie. **/
    Node declarator;

    /** Atribút initializer predstavuje vrchol pre hodnotu, ktorou inicializujeme declarator. **/
    Node initializer;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Declarator} a inicilizuje jej atribúty.
     *
     * @param decl vrchol pre deklarátor
     *
     * @param init vrchol pre inicializačnú hodnotu
     */
    public Declarator(Node decl, Node init) {
        this.declarator = decl;
        this.initializer = init;
    }

    /**
     * Metóda pre zistenie vrcholu inicializovanej premennej.
     * @return vrchol pre inicializovanú premennú
     */
    public Node getDeclarator() {
        return declarator;
    }

    /**
     * Metóda pre pridanie vrcholu inicializovanej premennej.
     * @param decl vrchol pre inicializovanú premennú
     */
    public void addDeclarator(Node decl) {
        declarator = decl;
    }

    /**
     * Metóda na zistenie vrchola inicializačnej hodnoty.
     * @return vrchol pre inicializačnú hodntu
     */
    public Node getInitializer() {
        return initializer;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Declarator: ");
        if (declarator != null) declarator.traverse(indent + "    ");
        if (initializer != null) initializer.traverse(indent + "    ");
    }

    public Node getBitsize() {
        return null;
    }

}
