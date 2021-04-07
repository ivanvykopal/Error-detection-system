package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca počiatočný vrchol abstraktného syntaktického stromu (Abstract syntax tree).
 * Predstavuje začiatok pre zdrojový kód v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class AST extends Node {
    /** Atribút exts obsahuje zoznam externých deklarácií. **/
    ArrayList<Node> exts;

    /**
     * Konštruktor, ktorý vytvára triedu {@code AST} a inicilizuje jej atribúty.

     * @param exts zoznam externých deklarácií
     */
    public AST(ArrayList<Node> exts) {
        this.exts = exts;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "AST: ");
        if (exts != null) {
            for (Node ext : exts) {
                ext.traverse(indent + "    ");
            }
        }
    }

}
