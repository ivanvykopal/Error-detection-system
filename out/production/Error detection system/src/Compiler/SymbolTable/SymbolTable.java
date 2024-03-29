package Compiler.SymbolTable;

import Backend.InternationalizationClass;
import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Trieda, ktorá obsahuje informácie o premenných, funkciách a parametroch.
 *
 * @author Ivan Vykopal
 */
public class SymbolTable implements Serializable {
    /** Atribút parent obsahuje referenciu na rodičovskú symbolickú tabuľku **/
    private SymbolTable parent = null;

    /** Atribút table obsahuje zoznam premenných, funkcií,... **/
    private HashMap<String, Record> table;

    /** Atribút childs predstavuje zoznam potomkov (symbolických tabuliek). **/
    private ArrayList<SymbolTable> childs = new ArrayList<>();

    /**
     * Konštruktor, v ktorom nastavujeme predchádzajúcu tabuľku.
     *
     * @param parent - rodičovská tabuľka
     */
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        table = new HashMap<>();
    }

    /**
     * Metóda na vyhľadanie hodnoty v symbolickej tabuľke.

     * @param key kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     *
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
     *
     * Zároveň sa kontroluje, či už zadaná premenná nie je v symbolickej tabuľke.
     *
     * @param key kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     *
     * @param value hodnota, ktorá je viazaná na kľúč
     *
     * @param line riadok, na ktorom sa identifikátor používa
     *
     * @param kind druh (premenná, paramter, funckia, ...)
     *
     * @param database databáza chýb
     */
    public void insert(String key, Record value, int line, byte kind, ErrorDatabase database) {
        if (this.parent == null && (kind == Kind.ARRAY_PARAMETER || kind == Kind.PARAMETER)) {
            return;
        }
        Record record = null;
        boolean isInSymbolTable = false;
        for (SymbolTable curr = this; curr != null; curr = curr.parent) {
            record = curr.table.get(key);
            if (record != null) {
                isInSymbolTable = record.getKind() != Kind.ENUMERATION_CONSTANT && record.getKind() != Kind.STRUCT_ARRAY_PARAMETER &&
                        record.getKind() != Kind.STRUCT_PARAMETER && record.getKind() != Kind.ARRAY_PARAMETER
                        && record.getKind() != Kind.PARAMETER && record.getKind() != Kind.FUNCTION && record.getKind() != Kind.TYPEDEF_NAME;
                break;
            }
        }
        if (kind == Kind.FUNCTION && record != null && record.getKind() == Kind.FUNCTION) {
            return;
        }

        if (!isInSymbolTable) {
            table.put(key, value);
        } else {
            database.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-SmA-02"), "E-SmA-02");
        }
    }

    /**
     * Metóda na zistenie, či sa v symbolickej tabuľke nachádzajú globálne premenné.
     *
     * @param identifier názov identifikátoru
     *
     * @return true, ak identifikátor je globálna premenná, inak false
     */
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
     *
     * @param key identifikátor
     *
     * @param typeCategory číselné vyjadrenie typu
     *
     * @param type typ
     *
     * @param line riadok deklarácie
     *
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
     *
     * @param key identifikátor
     *
     * @param typeCategory číselné vyjadrenie typu
     *
     * @param type typ
     *
     * @param line riadok deklarácie
     *
     * @param kind typ identifikátora
     *
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
     *
     * @param key identifikátor
     *
     * @param typeCategory číselné vyjadrenie typu
     *
     * @param type typ
     *
     * @param line riadok deklarácie
     *
     * @param kind typ identifikátora
     *
     * @param size veľkosť poľa
     *
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
     *
     * @param key kľúč, podľa ktorého sa vyhľadáva záznam v symbolickej tabuľke
     *
     * @param newValue nová hodnota, ktorá sa zapíše pre daný kľúč
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
     *
     * @return predchádzajúcu tabuľku
     */
    public SymbolTable getParent() {
        return parent;
    }

    /**
     * Metóda na nastavenie predchodcu.
     *
     * @param parent predchodca
     */
    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * Metóda na získanie HashTabuľky.
     *
     * @return hash tabuľka
     */
    public HashMap<String, Record> getTable() {
        return table;
    }

    /**
     * Metóda na pridanie vnorenej tabuľky.
     *
     * @param newSymbolTable vnorená tabuľka
     */
    public void addChild(SymbolTable newSymbolTable) {
        childs.add(newSymbolTable);
    }

    /**
     * Metóda na vrátenie symbolickej tabuľky
     *
     * @param index pozícia v ArrayListe
     *
     * @return symbolická tabuľka
     */
    public SymbolTable getChilds(int index) {
        return childs.get(index);
    }

    /**
     * Metóda na zistenie nasledovníkov danej tabuľky.
     *
     * @return nasledovníci tabuľky
     */
    public ArrayList<SymbolTable> getChilds() {
        return childs;
    }

    /**
     * Metóda pre vytvorenie kópie symbolickej tabuľky.
     *
     * @return kópia symbolickej tabuľky
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
            ProgramLogger.createLogger(SymbolTable.class.getName()).log(Level.WARNING,
                    InternationalizationClass.getBundle().getString("copyErr"));
            return null;
        }
    }

    /**
     * Metóda na vypísanie symbolickej tabuľky.
     *
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
     *
     * @param errorDatabase databáza chýb
     */
    public void findGlobalVariable(ErrorDatabase errorDatabase) {
        if (parent == null) {
            for (String key: table.keySet()) {
                Record record = table.get(key);
                if (record.getKind() == Kind.VARIABLE || record.getKind() == Kind.ARRAY) {
                    errorDatabase.addErrorMessage(record.getDeclarationLine(), InternationalizationClass.getErrors().getString("W-01"), "W-01");
                }
            }
        }
    }

    /**
     * Metóda na zistenie, či sa v programe nachádza dlho aktávna premenná.
     *
     * @param errorDatabase databáza chýb
     */
    public void findLongActiveVariable(ErrorDatabase errorDatabase) {
        Properties prop = new Properties();
        InputStream is;
        try {
            is = new FileInputStream("config/longActiveVariable.config");
        } catch (FileNotFoundException e) {
            is = getClass().getResourceAsStream("/config/longActiveVariable.config");
        }
        try {
            prop.load(is);
        } catch (IOException e) {
            ProgramLogger.createLogger(SymbolTable.class.getName()).log(Level.WARNING,
                    InternationalizationClass.getBundle().getString("configErr2"));
        }

        int initLine, usageLine;
        try {
            initLine = Integer.parseInt(prop.getProperty("initialization"));
            usageLine = Integer.parseInt(prop.getProperty("usage"));
        } catch (NumberFormatException e) {
            ProgramLogger.createLogger(SymbolTable.class.getName()).log(Level.WARNING,
                    InternationalizationClass.getBundle().getString("readErr2"));
            initLine = 3;
            usageLine = 10;
        }

        for (String key : table.keySet()) {
            Record record = table.get(key);
            if (record.getKind() == Kind.VARIABLE || record.getKind() == Kind.ARRAY) {
                if (record.getInitializationLines().size() > 0) {
                    if (record.getInitializationLines().get(0) - record.getDeclarationLine() > initLine) {
                        errorDatabase.addErrorMessage(record.getDeclarationLine(), InternationalizationClass.getErrors().getString("E-RP-05"), "E-RP-05");
                    }
                }
                if (record.getInitializationLines().size() > 0 && record.getUsageLines().size() > 0) {
                    if (record.getUsageLines().get(0) - record.getInitializationLines().get(0) > usageLine) {
                        errorDatabase.addErrorMessage(record.getDeclarationLine(), InternationalizationClass.getErrors().getString("E-RP-05"), "E-RP-05");
                    }
                }
            }
        }
        for (SymbolTable tab : childs) {
            tab.findLongActiveVariable(errorDatabase);
        }
    }
}