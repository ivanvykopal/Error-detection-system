package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre identifikátor v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Identifier extends Node {
    /** Atribút name presdstavuje názov identifikátora.**/
    String name;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Identifier} a inicilizuje jej atribúty.
     *
     * @param name názov identifikátora.
     *
     * @param line riadok využitia
     */
    public Identifier(String name, int line) {
        this.name = name;
        setLine(line);
    }

    /**
     * Metóda pre zistenie názvu identifikátora.
     *
     * @return názov identifikátora
     */
    public String getName() {
        return name;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Identifier: ");
        if (name != null)System.out.println(indent + name);
    }

}
