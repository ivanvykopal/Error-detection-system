package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre continue v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Continue extends Node {

    /**
     * Konštruktor, ktorý vytvára triedu {@code Continue} a inicilizuje jej atribút.
     *
     * @param line riadok využitia
     */
    public Continue(int line) {
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Continue: ");
    }

}
