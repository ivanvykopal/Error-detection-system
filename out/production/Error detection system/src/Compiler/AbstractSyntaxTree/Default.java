package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre default v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Default extends Node {
    /** Atribút statement obsahuje vrchol pre blok príkazov nasledovaný za default. **/
    Node statement;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Default} a inicilizuje jej atribúty.
     *
     * @param stmt vrchol pre blok príkazov
     *
     * @param line riadok využitia
     */
    public Default(Node stmt, int line) {
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
        System.out.println(indent + "Default: ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
