package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre zoznam deklarácií v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class DeclarationList extends Node {
    /** Atribút declaration obsahuje zoznam deklarácií. **/
    ArrayList<Node> declarations;

    /**
     * Konštruktor, ktorý vytvára triedu {@code DeclarationList} a inicilizuje jej atribúty.
     *
     * @param decls zoznam deklarácií
     *
     * @param line riadok využitia
     */
    public DeclarationList ( ArrayList<Node> decls, int line) {
        this.declarations = decls;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "DeclarationList: ");
        if (declarations != null) {
            for (Node decl : declarations) {
                decl.traverse(indent + "    ");
            }
        }
    }

}
