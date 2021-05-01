package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre epsilon v gramatike, alebo pre prázdny príkaz (;).
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class EmptyStatement extends Node {

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "EmptyStatement: ");
    }

    /**
     * Metóda pre zistenie, či ide o triedu EmptyStatement.
     *
     * @return true, ak ide o triedu {@code EmptyStatement}
     *         false, ak nejde o triedu {@code EmptyStatement}
     */
    @Override
    public boolean isEmpty() {
        return true;
    }

}
