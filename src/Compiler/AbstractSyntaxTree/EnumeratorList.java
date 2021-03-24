package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre zoznam enumerátorov.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class EnumeratorList extends Node {
    /** Atribút enumerators obsahuje zoznam enumerátorov. **/
    ArrayList<Node> enumerators;

    /**
     * Konštruktor, ktorý vytvára triedu {@code EnumeratorList} a inicilizuje jej atribúty.
     *
     * @param enums zoznam enumerátorov
     *
     * @param line riadok využitia
     */
    public EnumeratorList(ArrayList<Node> enums, int line) {
        this.enumerators = enums;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "EnumeratorList: ");
        if (enumerators != null) {
            for (Node enums : enumerators) {
                enums.traverse(indent + "    ");
            }
        }
    }

}
