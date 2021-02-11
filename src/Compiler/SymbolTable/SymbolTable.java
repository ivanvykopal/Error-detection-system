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
        type = extractAttribute(type);
        if (type.contains("typedef")) {
            type = type.replace("typedef ", "");
            record = new Record(findType(type), type, line, Kind.TYPEDEF_NAME);
        } else {
            record = new Record(findType(type), type, line, Kind.VARIABLE);
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
        type = extractAttribute(type);
        if (type.contains("typedef")) {
            type = type.replace("typedef ", "");
            record = new Record(findType(type), type, line, Kind.TYPEDEF_NAME);
        } else {
            record = new Record(findType(type), type, line, kind);
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
        type = extractAttribute(type);
        Record record = new Record(findType(type), type, line, kind);
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

        //riešenie typov
        switch (type.hashCode()) {
            case 3052374: return (byte) (Type.CHAR + pointer);                      // char
            case -359586342: return (byte) (Type.SIGNEDCHAR + pointer);             // signed char
            case 986197409: return (byte) (Type.UNSIGNEDCHAR + pointer);            // unsigned char
            case 109413500: return (byte) (Type.SHORT + pointer);                   // short
            case 1752515192: return (byte) (Type.SIGNEDSHORT + pointer);            // signed short
            case 522138513: return (byte) (Type.UNSIGNEDSHORT + pointer);           // unsigned short
            case 104431: return (byte) (Type.INT + pointer);                        // int
            case -902467812: return (byte) (Type.SIGNED + pointer);                 // signed
            case -981424917: return (byte) (Type.SIGNEDINT + pointer);              // signed int
            case -15964427: return (byte) (Type.UNSIGNED + pointer);                // unsigned
            case 1140197444: return (byte) (Type.UNSIGNEDINT + pointer);            // unsigned int
            case -2029581749: return (byte) (Type.SHORTINT + pointer);              // short int
            case -827364793: return (byte) (Type.SIGNEDSHORTINT + pointer);         // signed short int
            case 1314465504: return (byte) (Type.UNSIGNEDSHORTINT + pointer);       // unsigned short int
            case 3327612: return (byte) (Type.LONG + pointer);                      // long
            case -359311104: return (byte) (Type.SIGNEDLONG + pointer);             // signed long
            case 986472647: return (byte) (Type.UNSIGNEDLONG + pointer);            // unsigned long
            case -2075964341: return (byte) (Type.LONGINT + pointer);               // long int
            case 2119236815: return (byte) (Type.SIGNEDLONGINT + pointer);          // signed long int
            case 1218496790: return (byte) (Type.UNSIGNEDLONGINT + pointer);        // unsigned long int
            case 69705120: return (byte) (Type.LONGLONG + pointer);                 // long long
            case 1173352815: return (byte) (Type.LONGLONGINT + pointer);            // long long int
            case 1271922076: return (byte) (Type.SIGNEDLONGLONG + pointer);         // signed long long
            case -1037044885: return (byte) (Type.SIGNEDLONGLONGINT + pointer);     // signed long long int
            case -881214923: return (byte) (Type.UNSIGNEDLONGLONG + pointer);       // unsigned long long
            case -1492665468: return (byte) (Type.UNSIGNEDLONGLONGINT + pointer);   // unsigned long long int
            case 97526364: return (byte) (Type.FLOAT + pointer);                    // float
            case -1325958191: return (byte) (Type.DOUBLE + pointer);                // double
            case -1961682443: return (byte) (Type.LONGDOUBLE + pointer);            // long double
            case 111433423: return (byte) (Type.UNION + pointer);                   // union
            case -891974699: return (byte) (Type.STRUCT + pointer);                 // struct
            case 3118337: return (byte) (Type.ENUM + pointer);                      // enum
            case 3625364: return (byte) (Type.VOID + pointer);                      // void
            default: return Type.TYPEDEF_TYPE;                                      // vlastný typ
        }
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
}