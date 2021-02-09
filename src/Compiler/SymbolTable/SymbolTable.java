package Compiler.SymbolTable;

import Compiler.Lexer.Tag;

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
     *
     * @param key
     * @param type
     * @param line
     */
    public void insert(String key, String type, int line) {
        Record record = new Record(findType(type), line, Kind.VARIABLE);
        insert(key, record);
    }

    /**
     *
     * @param key
     * @param type
     * @param line
     * @param kind
     */
    public void insert(String key, String type, int line, byte kind) {
        Record record = new Record(findType(type), line, kind);
        insert(key, record);
    }

    /**
     *
     * @param key
     * @param type
     * @param line
     * @param kind
     * @param size
     */
    public void insert(String key, String type, int line, byte kind, int size) {
        Record record = new Record(findType(type), line, kind);
        record.setSize(size);
        insert(key, record);
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

    //TODO: vymyslieť lepší spôsob -> vymyslieť vlastný hash!!
    private byte findType(String type) {
        HashMap<String, Byte> types = new HashMap<String, Byte>(){{
            put("char ", Type.CHAR);
            put("signed char ", Type.SIGNEDCHAR);
            put("unsigned char ", Type.UNSIGNEDCHAR);
            put("short ", Type.SHORT);
            put("signed short ", Type.SIGNEDSHORT);
            put("unsigned short", Type.UNSIGNEDSHORT);
            put("int ", Type.INT);
            put("signed ", Type.SIGNED);
            put("signed int ", Type.SIGNEDINT);
            put("unsigned ", Type.UNSIGNED);
            put("unsigned int ", Type.UNSIGNEDINT);
            put("short int ", Type.SHORTINT);
            put("signed short int ", Type.SIGNEDSHORTINT);
            put("unsigned short int ", Type.UNSIGNEDSHORTINT);
            put("long", Type.LONG);
            put("signed long", Type.SIGNEDLONG);
            put("unsigned long", Type.UNSIGNEDLONG);
            put("long int", Type.LONGINT);
            put("signed long int", Type.SIGNEDLONGINT);
            put("unsigned long int", Type.UNSIGNEDLONGINT);
            put("long long", Type.LONGLONG);
            put("long long int", Type.LONGLONGINT);
            put("signed long long", Type.SIGNEDLONGLONG);
            put("signed long long int", Type.SIGNEDLONGLONGINT);
            put("unsigned long long", Type.UNSIGNEDLONGLONG);
            put("unsigned long long int", Type.UNSIGNEDLONGLONGINT);
            put("float", Type.FLOAT);
            put("double", Type.DOUBLE);
            put("long double", Type.LONGDOUBLE);
            put("union", Type.UNION);
            put("struct", Type.STRUCT);
            put("enum", Type.ENUM);
        }};

        return types.get(type);
    }
}
