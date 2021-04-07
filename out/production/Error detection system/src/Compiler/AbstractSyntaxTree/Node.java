package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.SymbolTable;

/**
 * Trieda predstavujúca vrchol v abstraktnom syntaktickom strome (Abstract syntax tree)
 *
 * @author Ivan Vykopal
 */
public abstract class Node {
    /** Atribút line predstavuje riadok využitia. **/
    protected int line;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Node}.
     */
    public Node() {
    }

    /**
     * Abstraktná metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    abstract public void traverse(String indent);

    /**
     * Metóda na zistienie, či daný vrchol je trieda {@code None}.
     *
     * @return true, ak ide o triedu {@code None}
     *         false, ak nejde o triedu {@code None}
     */
    public boolean isNone() {
        return false;
    }

    /**
     * Metóda na zistienie, či daný vrchol je trieda {@code EmptyStatement}.
     *
     * @return true, ak ide o triedu {@code EmptyStatement}
     *         false, ak nejde o triedu {@code EmptyStatement}
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Metóda na zistienie, či daný vrchol je trieda {@code Enum}, {@code Struct} alebo {@code Union}.
     *
     * @return true, ak ide o triedu {@code Enum}, {@code Struct} alebo {@code Union}
     *         false, ak nejde o triedu {@code Enum}, {@code Struct} alebo {@code Union}
     */
    public boolean isEnumStructUnion() {
        return false;
    }

    /**
     * Metóda na zistenie vrcholu pre typ.
     *
     * @return vrchol pre typ
     */
    public Node getType() {
        return null;
    }

    /**
     * Metóda na zistenie vrcholu názvu.
     *
     * @return vrchol názvu.
     */
    public Node getNameNode() {
        return null;
    };

    /**
     * Metóda na nastavenie typu.
     *
     * @param type typ
     */
    public void addType(Node type) {
    }

    /**
     * Metóda pre pridanie yužitia premenných v rámci {@code Assignment}, pre zadaný riadok.
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, na ktorom sa premenné využívajú
     */
    public void resolveUsage(SymbolTable table, int line) {
    }

    /**
     * Metóda na nastavenie riadku využitia.
     *
     * @param line riadok využitia
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Metóda na zistenie riadku využitia.
     *
     * @return riadok využitia
     */
    public int getLine() {
        return line;
    }

}