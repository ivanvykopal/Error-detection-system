package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre chybu.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Err extends Node {

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Err:");
    }

}