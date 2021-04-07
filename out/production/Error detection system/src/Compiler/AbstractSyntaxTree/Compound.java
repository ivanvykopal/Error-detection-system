package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre blok v jazyku C. Blok je reprezentovaný  kučeravými zátvorkami ( {, }).
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Compound extends Node {
    /** Atribút statements obsahuje zoznam príkazov. **/
    ArrayList<Node> statements;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Compound} a inicilizuje jej atribúty.
     *
     * @param stmts zoznam príkazov
     *
     * @param line riadok využitia
     */
    public Compound(ArrayList<Node> stmts, int line) {
        this.statements = stmts;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Compound: ");
        if (statements != null) {
            for (Node stmt : statements) {
                stmt.traverse(indent + "    ");
            }
        }
    }

}
