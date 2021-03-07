package Compiler.SymbolTable;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Trieda, ktorá obsahuje informácie o premenných, funkciách a parametroch.
 */
public class SymbolTable implements Cloneable {
    SymbolTable parent = null;
    HashMap<String, Record> table;
    ArrayList<SymbolTable> childs = new ArrayList<>();
    Error err = new Error();


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
        Record record;
        for (SymbolTable curr = this; curr != null; curr = curr.parent) {
            record = curr.table.get(key);
            if (record != null) {
                return record;
            }
        }
        return null;
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key - kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     * @param value - hodnota, ktorá je viazaná na kľúč
     */
    public void insert(String key, Record value, int line, byte kind, ErrorDatabase database) {
        Record record = null;
        boolean isInSymbolTable = false;
        for (SymbolTable curr = this; curr != null; curr = curr.parent) {
            record = curr.table.get(key);
            if (record != null && kind != Kind.PARAMETER && kind != Kind.ARRAY_PARAMETER) {
                isInSymbolTable = record.getKind() != Kind.ENUMERATION_CONSTANT && record.getKind() != Kind.STRUCT_ARRAY_PARAMETER &&
                        record.getKind() != Kind.STRUCT_PARAMETER && record.getKind() != Kind.ARRAY_PARAMETER
                        && record.getKind() != Kind.PARAMETER;
            }
            if (record != null && (kind == Kind.PARAMETER || kind == Kind.ARRAY_PARAMETER)) {
                SymbolTable current = this;

                while (current.parent != null) {
                    current = current.parent;
                }

                current.table.put(key, value);
                return;
            }
        }
        if (kind == Kind.FUNCTION && record != null && record.getKind() == Kind.FUNCTION) {
            return;
        }

        if (!isInSymbolTable) {
            table.put(key, value);
        } else {
            System.out.println("Chyba na riadku " + line + ": Viacnásobná deklarácia premennej!");
            database.addErrorMessage(line, err.getError("E-SmA-02"), "E-SmA-02");
        }
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param type typ
     * @param line riadok deklarácie
     */
    public void insert(String key, byte typeCategory, String type, int line, ErrorDatabase database) {
        Record record;
        type = extractAttribute(type);
        if (type.contains("typedef")) {
            type = type.replace("typedef ", "");
            record = new Record(typeCategory, type, line, Kind.TYPEDEF_NAME);
            insert(key, record, line, Kind.TYPEDEF_NAME, database);
        } else {
            record = new Record(typeCategory, type, line, Kind.VARIABLE);
            insert(key, record, line, Kind.VARIABLE, database);
        }
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param type typ
     * @param line riadok deklarácie
     * @param kind typ identifikátora
     */
    public void insert(String key, byte typeCategory, String type, int line, byte kind, ErrorDatabase database) {
        Record record;
        type = extractAttribute(type);
        if (type.contains("typedef")) {
            type = type.replace("typedef ", "");
            record = new Record(typeCategory, type, line, Kind.TYPEDEF_NAME);
        } else {
            record = new Record(typeCategory, type, line, kind);
        }
        insert(key, record, line, kind, database);
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param type typ
     * @param line riadok deklarácie
     * @param kind typ identifikátora
     * @param size veľkosť poľa
     */
    public void insert(String key, byte typeCategory, String type, int line, byte kind, int size, ErrorDatabase database) {
        type = extractAttribute(type);
        Record record = new Record(typeCategory, type, line, kind);
        record.setSize(size);
        insert(key, record, line, kind, database);
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
        Record record;
        for (SymbolTable curr = this; curr != null; curr = curr.parent) {
            record = curr.table.get(key);
            if (record != null) {
                curr.table.replace(key, newValue);
                break;
            }
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

    private String extractAttribute(String type) {
        //TODO: nájsť efektívnejšie riešenie
        //riešenie EXTERN, STATIC, AUTO, REGISTER, CONST, VOLATILE
        if (type.contains("extern")) {
            type = type.replace("extern ", "");
        } else if (type.contains("static")) {
            type = type.replace("static ", "");
        } else if (type.contains("auto ")) {
            type = type.replace("auto ", "");
        } else if (type.contains("register")) {
            type = type.replace("register ", "");
        } else if (type.contains("const")) {
            type = type.replace("const ", "");
        } else if (type.contains("volatile")) {
            type = type.replace("volatile ", "");
        }
        return type;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}