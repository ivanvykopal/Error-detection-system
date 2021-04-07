package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre typ.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class IdentifierType extends Node {
    /** Atribút names predstavuje zoznam typov (int, char, ...) **/
    ArrayList<String> names;

    /**
     * Konštruktor, ktorý vytvára triedu {@code IdentifierType} a inicilizuje jej atribúty.
     *
     * @param name zoznam typov
     *
     * @param line riadok využitia
     */
    public IdentifierType(ArrayList<String> name, int line) {
        this.names = name;
        setLine(line);
    }

    /**
     * Metóda ne zistenie typu zo zoznamu typov.
     *
     * @param index index typu
     *
     * @return typ na základe indexu
     */
    public String getName(int index) {
        return names.get(index);
    }

    /**
     * Metóda na zistenie zoznamu typov.
     *
     * @return zoznam typov
     */
    public ArrayList<String> getNames() {
        return names;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Identifier Type");
        if (names != null) {
            System.out.print(indent);
            for (String name : names) {
                System.out.print(name + ", ");
            }
            System.out.print("\n");
        }
    }

}
