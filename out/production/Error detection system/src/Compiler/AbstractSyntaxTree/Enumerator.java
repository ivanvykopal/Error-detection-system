package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre enumerator v jazyku C.
 * Enumerator predstavuje hodnotu nachádzajúcu sa v enum.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Enumerator extends Node {
    /** Atribút name obsahuje názov premennej enumerátora.**/
    String name;

    /** Atribút value obsahuje vrchol pre hodnotu enumeratora.**/
    Node value;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Enumerator} a inicilizuje jej atribúty.
     *
     * @param name názov premennej enumerátora
     *
     * @param value vrchol pre hodnotu enumerátora
     *
     * @param line riadok využitia
     */
    public Enumerator(String name, Node value, int line) {
         this.name = name;
         this.value = value;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Enumerator: ");
        if (name != null) System.out.println(indent + name);
        if (value != null) value.traverse(indent + "    ");
    }

}
