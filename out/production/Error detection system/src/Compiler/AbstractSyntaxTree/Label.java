package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre návestie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Label extends Node {
    /** Atribút name obsahuje názov návestia. **/
    String name;

    /** Atribút statement obsahuje vrchol pre blok príkazov. **/
    Node statement;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Label} a inicilizuje jej atribúty.
     *
     * @param name názov návestia
     *
     * @param stmt vrchol pre blok príkazov
     *
     * @param line riadok využitia
     */
    public Label(String name, Node stmt, int line) {
        this.name = name;
        this.statement = stmt;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Label: ");
        if (name != null) System.out.println(indent + name);
        if (statement != null) statement.traverse(indent + "    ");
    }

}
