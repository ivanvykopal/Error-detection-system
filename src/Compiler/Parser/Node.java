package Compiler.Parser;

import java.util.ArrayList;

public class Node {
    private String production;
    private ArrayList<Node> childs;

    /**
     * Konštruktor na nastavenie produkčného pravidla.
     * @param production produkčné pravidlo
     */
    public Node(String production) {
        this.production = production;
    }

    /**
     * Funkcia na pridanie nasledovníkov do stromu.
     * @param childs pole nasledovníkov
     */
    public void addChilds(ArrayList<Node> childs) {
        this.childs = childs;
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