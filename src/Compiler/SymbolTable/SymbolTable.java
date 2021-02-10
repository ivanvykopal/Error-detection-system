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
    public void insert(String key, Record value) {
        table.put(key, value);
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param type typ
     * @param line riadok deklarácie
     */
    public void insert(String key, String type, int line) {
        Record record;
        if (type.contains("typedef")) {
            record = new Record(findType(type), line, Kind.TYPEDEF_NAME);
        } else {
            record = new Record(findType(type), line, Kind.VARIABLE);
        }
        insert(key, record);
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param type typ
     * @param line riadok deklarácie
     * @param kind typ identifikátora
     */
    public void insert(String key, String type, int line, byte kind) {
        Record record;
        if (type.contains("typedef")) {
             record = new Record(findType(type), line, Kind.TYPEDEF_NAME);
        } else {
            record = new Record(findType(type), line, kind);
        }
        insert(key, record);
    }

    /**
     * Funkcia na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param type typ
     * @param line riadok deklarácie
     * @param kind typ identifikátora
     * @param size veľkosť poľa
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

    /**
     * Funkcia na zistenie typu.
     * @param type typ (String)
     * @return typ (byte)
     */
    private byte findType(String type) {
        //vymazanie poslednej medzery
        type = type.substring(0, type.length() - 1);
        byte pointer = 0;
        //riešenie smerníkov
        if (type.contains("*")) {
            pointer = 100;
            type = type.replace("* ", "");
        }

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

        //riešenie typov
        switch (type.hashCode()) {
            case 3052374: return (byte) (Type.CHAR + pointer);
            case -359586342: return (byte) (Type.SIGNEDCHAR + pointer);
            case 986197409: return (byte) (Type.UNSIGNEDCHAR + pointer);
            case 109413500: return (byte) (Type.SHORT + pointer);
            case 1752515192: return (byte) (Type.SIGNEDSHORT + pointer);
            case 522138513: return (byte) (Type.UNSIGNEDSHORT + pointer);
            case 104431: return (byte) (Type.INT + pointer);
            case -902467812: return (byte) (Type.SIGNED + pointer);
            case -981424917: return (byte) (Type.SIGNEDINT + pointer);
            case -15964427: return (byte) (Type.UNSIGNED + pointer);
            case 1140197444: return (byte) (Type.UNSIGNEDINT + pointer);
            case -2029581749: return (byte) (Type.SHORTINT + pointer);
            case -827364793: return (byte) (Type.SIGNEDSHORTINT + pointer);
            case 1314465504: return (byte) (Type.UNSIGNEDSHORTINT + pointer);
            case 3327612: return (byte) (Type.LONG + pointer);
            case -359311104: return (byte) (Type.SIGNEDLONG + pointer);
            case 986472647: return (byte) (Type.UNSIGNEDLONG + pointer);
            case -2075964341: return (byte) (Type.LONGINT + pointer);
            case 2119236815: return (byte) (Type.SIGNEDLONGINT + pointer);
            case 1218496790: return (byte) (Type.UNSIGNEDLONGINT + pointer);
            case 69705120: return (byte) (Type.LONGLONG + pointer);
            case 1173352815: return (byte) (Type.LONGLONGINT + pointer);
            case 1271922076: return (byte) (Type.SIGNEDLONGLONG + pointer);
            case -1037044885: return (byte) (Type.SIGNEDLONGLONGINT + pointer);
            case -881214923: return (byte) (Type.UNSIGNEDLONGLONG + pointer);
            case -1492665468: return (byte) (Type.UNSIGNEDLONGLONGINT + pointer);
            case 97526364: return (byte) (Type.FLOAT + pointer);
            case -1325958191: return (byte) (Type.DOUBLE + pointer);
            case -1961682443: return (byte) (Type.LONGDOUBLE + pointer);
            case 111433423: return (byte) (Type.UNION + pointer);
            case -891974699: return (byte) (Type.STRUCT + pointer);
            case 3118337: return (byte) (Type.ENUM + pointer);
            case 3625364: return (byte) (Type.VOID + pointer);
        }

        return -1;
    }
}