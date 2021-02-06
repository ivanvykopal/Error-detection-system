package Compiler.Parser;

import java.util.ArrayList;

public class Production extends Node {
    private String production;
    private ArrayList<Node> childs;

    /**
     * Konštruktor na nastavenie produkčného pravidla.
     * @param production produkčné pravidlo
     */
    public Production(String production) {
        this.production = production;
    }

    /**
     * Konštruktor bez parametrov.
     */
    public Production() {
    }

    /**
     * Funkcia na pridanie nasledovníkov do stromu.
     * @param child nasledovník
     */
    public void addChilds(Node child) {
        this.childs.add(child);
    }

    /**
     * Funkcia na zistenie nasledovníkov vrcholu.
     * @return pole nasledovníkov vrcholu
     */
    public ArrayList<Node> getChilds() {
        return this.childs;
    }

    /**
     * Funkcia na nastavenie produkčného pravidla.
     * @param production produkčné pravidlo
     */
    public void setProduction(String production) {
        this.production = production;
    }

    /**
     * Funkcia na zistenie produkčného pravidla.
     * @return produkčné pravidlo
     */
    public String getProduction() {
        return this.production;
    }
}