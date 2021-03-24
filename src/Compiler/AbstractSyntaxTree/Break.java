package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre break v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Break extends Node {

    /**
     * Konštruktor, ktorý vytvára triedu {@code Break} a inicilizuje jej atribúty.
     *
     * @param line riadok využitia
     */
    public Break(int line) {
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Break: ");
    }

}
