package Compiler.SymbolTable;

import java.util.HashMap;

/**
 * Trieda, ktorá obsahuje informácie o premenných, funkciách a parametroch.
 */
public class SymbolTable {
    SymbolTable parent = null;
    HashMap<String, Record> table;
    /**
     * Konštruktor, v ktorom nastavujeme predchádzajúcu tabuľku.
     * @param parent rodičovská tabuľka
     */
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        table = new HashMap<>();
    }

    public Record lookup(String key) {
        return table.get(key);
    }

    public void insert(String key, Record value) {
        table.put(key, value);
    }

    public void free() {
        table.clear();
    }

    public void setValue(String key, Record newValue) {
        Record item = table.replace(key, newValue);
        if (item == null) {
            table.put(key, newValue);
        }
    }
}