package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre goto v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Goto extends Node {
    /**Atribút name predstavuje label pre skok. **/
    String name;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Goto} a inicilizuje jej atribúty.
     *
     * @param name label pre skok
     *
     * @param line riadok využitia
     */
    public Goto(String name, int line) {
        this.name = name;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Goto: ");
        if (name != null) System.out.println(indent + name);
    }

}
