package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre smerník v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class PointerDeclaration extends Node {
    /** Atribút qualifiers predstavuje zoznam kvalifikátorov (volatile, const) **/
    ArrayList<String> qualifiers;

    /** Atribút type predstavuje vrchol pre typ smerníka. **/
    Node type;

    /**
     * Konštruktor, ktorý vytvára triedu {@code PointerDeclaration} a inicilizuje jej atribúty.
     *
     * @param quals zoznam kvalifikátorov (volatile, const)
     *
     * @param type vrchol pre typ smerníka
     *
     * @param line riadok využitia
     */
    public PointerDeclaration(ArrayList<String> quals, Node type, int line) {
        this.qualifiers = quals;
        this.type = type;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "PointerDeclaration: ");
        System.out.print(indent);
        if (qualifiers != null) {
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
        if (type != null) type.traverse(indent + "    ");
    }

    /**
     * Metóda na zistenie vrcholu pre typ smerníka.
     *
     * @return vrchol pre typ smerníka
     */
    @Override
    public Node getType() {
        return type;
    }

    /**
     * Metóda na nastavenie typu smerníka.
     *
     * @param type vrchol pre typ smerníka
     */
    @Override
    public void addType(Node type) {
        this.type = type;
    }

}
