package Compiler.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Trieda, ktorá obsahuje informácie o premenných, funkciách a parametroch.
 */
public class SymbolTable {
    SymbolTable parent = null;
    HashMap<String, Record> table;
    ArrayList<SymbolTable> childs = null;

    /**
     * Konštruktor, v ktorom nastavujeme predchádzajúcu tabuľku.
     * @param parent - rodičovská tabuľka
     */
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        table = new HashMap<>();
    }

    /**
     * Funkcia na vyhľadanie hodnoty v symbolickej tabuľke.
     * @param key - kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     * @return záznam zo symbolickej tabuľky
     */
    public Record lookup(String key) {
        return table.get(key);
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key - kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     * @param value - hodnota, ktorá je viazaná na kľúč
     */
    public void insert(String key, Record value) {
        table.put(key, value);
    }

    /**
     * Funkcia na vyprázdnenie symbolickej tabuľky.
     */
    public void free() {
        table.clear();
    }

    /**
     * Funkcia na zmenu hodnoty v zázname.
     * @param key - kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     * @param newValue - nová hodnota, ktorá sa zapíše pre daný kľúč
     */
    public void setValue(String key, Record newValue) {
        Record item = table.replace(key, newValue);
        if (item == null) {
            table.put(key, newValue);
        }
    }

    /**
     * Funkcia na pridanie vnorenej tabuľky.
     * @param newSymbolTable vnorená tabuľka
     */
    public void addChild(SymbolTable newSymbolTable) {
        childs.add(newSymbolTable);
    }

    /**
     * Funkcia na vrátenie symbolickej tabuľky
     * @param index pozícia v ArrayListe
     * @return symbolická tabuľka
     */
    public SymbolTable getChilds(int index) {
        return childs.get(index);
    }
}
