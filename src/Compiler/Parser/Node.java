package Compiler.Parser;

import java.util.ArrayList;

public abstract class Node {

    /**
     * Konštruktor bez parametrov.
     */
    public Node() {
    }

    /**
     * Funkcia na zistenie nasledovníkov vrcholu.
     * @return pole nasledovníkov vrcholu
     */
    abstract public ArrayList<Node> getChilds();

    abstract public void printData(String indent);

}