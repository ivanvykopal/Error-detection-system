package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre deklaráciu poľa v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class ArrayDeclaration extends Node {
    /** Atribút type obsahuje informácie o type poľa. **/
    Node type;

    /** Atribút dimension obsahuje informácie o veľkosti poľa.**/
    Node dimension;

    /** Atribút dimensionQualifiers obsahuje informácie o kvalifikátoroch nachádzajúcich sa v rámci []. **/
    ArrayList<String> dimensionQualifiers;

    /**
     * Konštruktor, ktorý vytvára triedu {@code ArrayDeclaration} a inicializuje je atribúty .
     *
     * @param type vrchol pre typ poľa
     *
     * @param dim vrchol pre veľkosť poľa
     *
     * @param dims kvalifikátory poľa
     *
     * @param line riadok deklarácie
     */
    public ArrayDeclaration(Node type, Node dim, ArrayList<String> dims, int line) {
        this.type = type;
        this.dimension = dim;
        this.dimensionQualifiers = dims;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ArrayDeclaration: ");
        if (type != null) type.traverse(indent + "    ");
        if (dimension != null) dimension.traverse(indent + "    ");
    }

    /**
     * Metóda, ktorá vracia vrchol pre veľkosť deklarovaného poľa.
     *
     * @return vrchol veľkosti poľa
     */
    public Node getDimension() {
        return dimension;
    }

    /**
     * Metóda, ktorá vracia vrchol pre typ deklarovaného poľa.
     *
     * @return vrchol pre typ deklarovaného poľa
     */
    @Override
    public Node getType() {
        return type;
    }

    /**
     * Metóda prostredníctvom, ktorej pridávame typ deklarovanému poľu.
     *
     * @param type vrchol pre typ deklarovaného poľa
     */
    @Override
    public void addType(Node type) {
        this.type = type;
    }

}