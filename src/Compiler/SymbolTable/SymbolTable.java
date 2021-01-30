package Compiler.SymbolTable;

import java.util.HashMap;

/**
 * Trieda, ktorá obsahuje informácie o premenných, funkciách a parametroch.
 */
public class SymbolTable {
    SymbolTable parent = null;
    HashMap table;
    /**
     * Konštruktor, v ktorom nastavujeme predchádzajúcu tabuľku.
     * @param parent rodičovská tabuľka
     */
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        table = new HashMap();
    }
}