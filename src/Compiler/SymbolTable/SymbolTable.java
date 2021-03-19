package Compiler.SymbolTable;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Trieda, ktorá obsahuje informácie o premenných, funkciách a parametroch.
 */
public class SymbolTable implements Serializable {
    SymbolTable parent = null;
    HashMap<String, Record> table;
    ArrayList<SymbolTable> childs = new ArrayList<>();

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
     * Metóda na vloženie záznamu do symbolickej tabuľky.
     * @param key kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     * @param value hodnota, ktorá je viazaná na kľúč
     * @param line riadok, na ktorom sa identifikátor používa
     * @param kind typ
     * @param database databáza chýb
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
        }
        if (kind == Kind.FUNCTION && record != null && record.getKind() == Kind.FUNCTION) {
            return;
        }

        if (!isInSymbolTable) {
            table.put(key, value);
        } else {
            System.out.println("Chyba na riadku " + line + ": Viacnásobná deklarácia premennej!");
            database.addErrorMessage(line, Error.getError("E-SmA-02"), "E-SmA-02");
        }
    }

    public boolean isGlobal(String identifier) {
        SymbolTable curr = this;
        while (curr.parent != null) {
            curr = curr.parent;
        }
        Record record = curr.getTable().get(identifier);
        return record != null;
    }

    /**
     * Metóda na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param typeCategory číselné vyjadrenie typu
     * @param type typ
     * @param line riadok deklarácie
     * @param database databáza chýb
     */
    public void insert(String key, short typeCategory, String type, int line, ErrorDatabase database) {
        Record record;
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
     * Metóda na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param typeCategory číselné vyjadrenie typu
     * @param type typ
     * @param line riadok deklarácie
     * @param kind typ identifikátora
     * @param database databáza chýb
     */
    public void insert(String key, short typeCategory, String type, int line, byte kind, ErrorDatabase database) {
        Record record;
        if (type.contains("typedef")) {
            type = type.replace("typedef ", "");
            record = new Record(typeCategory, type, line, Kind.TYPEDEF_NAME);
        } else {
            record = new Record(typeCategory, type, line, kind);
        }
        insert(key, record, line, kind, database);
    }

    /**
     * Metóda na vloženie záznamu do symbolickej tabuľky.
     * @param key identifikátor
     * @param typeCategory číselné vyjadrenie typu
     * @param type typ
     * @param line riadok deklarácie
     * @param kind typ identifikátora
     * @param size veľkosť poľa
     * @param database databáza chýb
     */
    public void insert(String key, short typeCategory, String type, int line, byte kind, int size, ErrorDatabase database) {
        Record record = new Record(typeCategory, type, line, kind);
        record.setSize(size);
        insert(key, record, line, kind, database);
    }

    /**
     * Metóda na vyprázdnenie symbolickej tabuľky.
     */
    public void free() {
        table.clear();
    }

    /**
     * Metóda na zmenu hodnoty v zázname.
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
     * Metóda na zistenie predchodcu.
     * @return predchádzajúcu tabuľku
     */
    public SymbolTable getParent() {
        return parent;
    }

    /**
     * Metóda na nastavenie predchodcu
     * @param parent predchodca
     */
    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * Metóda na získanie HashTabuľky.
     * @return hash tabuľka
     */
    public HashMap<String, Record> getTable() {
        return table;
    }

    /**
     * Metóda na pridanie vnorenej tabuľky.
     * @param newSymbolTable vnorená tabuľka
     */
    public void addChild(SymbolTable newSymbolTable) {
        childs.add(newSymbolTable);
    }

    /**
     * Metóda na vrátenie symbolickej tabuľky
     * @param index pozícia v ArrayListe
     * @return symbolická tabuľka
     */
    public SymbolTable getChilds(int index) {
        return childs.get(index);
    }

    /**
     * Metóda na zistenie nasledovníkov danej tabuľky.
     * @return nasledovníci tabuľky
     */
    public ArrayList<SymbolTable> getChilds() {
        return childs;
    }

    /**
     * Metóda pre vytvorenie kópie symbolickej tabuľky.
     * @return
     */
    public SymbolTable createCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (SymbolTable) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metóda na vypísanie symbolickej tabuľky.
     * @param depth hĺbka symbolickej v tabuľke v strome
     */
    public void printSymbolTable(int depth) {
        System.out.println("--------Symbolická tabuľka v hĺbke " + depth + " --------");
        for (String key: table.keySet()) {
            Record record = table.get(key);
            System.out.println("----\n"  + "Názov: " + key);
            System.out.println(record.toString());
        }
        for (SymbolTable child: childs) {
            child.printSymbolTable(depth + 1);
        }
    }

    /**
     * Metóda na zistenie, či sa v programe nachádza globálna premenná.
     * @param errorDatabase databáza chýb
     */
    public void findGlobalVariable(ErrorDatabase errorDatabase) {
        if (parent == null) {
            for (String key: table.keySet()) {
                Record record = table.get(key);
                if (record.getKind() == Kind.VARIABLE || record.getKind() == Kind.ARRAY) {
                    errorDatabase.addErrorMessage(record.getDeclarationLine(), Error.getError("W-01"), "W-01");
                }
            }
        }
    }

    /**
     * Metóda na zistenie, či sa v programe nachádza dlho aktávna premenná.
     * @param errorDatabase databáza chýb
     */
    public void findLongActiveVariable(ErrorDatabase errorDatabase) {
        for (String key : table.keySet()) {
            Record record = table.get(key);
            if (record.getKind() == Kind.VARIABLE || record.getKind() == Kind.ARRAY) {
                //TODO: aktávna premenná ak je viac ako 10 riadkov, viem meniť
                // -malý počet riadkov ak nie je hneď inicializovaná
                if (record.getInitializationLines().size() > 0) {
                    if (record.getInitializationLines().get(0) - record.getDeclarationLine() > 3) {
                        errorDatabase.addErrorMessage(record.getDeclarationLine(), Error.getError("E-RP-05"), "E-RP-05");
                    }
                }
                // - nechať tak
                if (record.getInitializationLines().size() > 0 && record.getUsageLines().size() > 0) {
                    if (record.getUsageLines().get(0) - record.getInitializationLines().get(0) > 10) {
                        errorDatabase.addErrorMessage(record.getDeclarationLine(), Error.getError("E-RP-05"), "E-RP-05");
                    }
                }
            }
        }
        for (SymbolTable tab : childs) {
            tab.findGlobalVariable(errorDatabase);
        }
    }
}