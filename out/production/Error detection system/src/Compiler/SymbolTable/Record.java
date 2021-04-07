package Compiler.SymbolTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Trieda obsahujúca informácie o identifikátore.
 * - dátový typ, riadok deklarovania, hodnota pri deklarácii, prvé využitie a typ (premenná, pole, ...)
 * Pre pole sa pridáva zoznam parametrov a veľkosť poľa.
 *
 * @author Ivan Vykopal
 */
public class Record implements Serializable {
    /** Atribút type predstavuje typ (numerická hodnota). **/
    private short type;

    /** Atribút typeString predstavuje typ v textovej podobe (int, char, ...). **/
    private String typeString;

    /** Atribút declarationLine predstavuje riadok deklarácie. **/
    private int declarationLine;

    /** Atribút initialized predstavuje informáciu o tom, či premenná je inicializovaná **/
    private boolean initialized = false;

    /** Atribút usageLines predstavuje zoznam riadkov, na ktorých sa premenná využíva. **/
    private ArrayList<Integer> usageLines;

    /** Atribút initializationLines predstavuje zoznam riadkov, na ktorých sa premenná inicializuje. **/
    private ArrayList<Integer> initializationLines;

    /** Atribút kind predstavuje druh (premenná, pole, parameter, ...) **/
    private byte kind;

    /** Atribút parameters predstavuje zoznam parametrov funkcie. **/
    private ArrayList<String> parameters = new ArrayList<>();

    /** Atribút size predstavuje veľkosť zadeklarovaného poľa. **/
    private int size = 0;

    /**
     * Konštruktor, v ktorom nastavujeme základné hodnoty.
     *
     * @param type dátovy typ premennej, resp. návratová hodnota
     *
     * @param typeString dátový typ premennej (String)
     *
     * @param line riadok deklarácie
     *
     * @param initialized hodnota pri deklarácii
     *
     * @param kind typ (premenná, pole, funkcia, parameter)
     */
    public Record(short type, String typeString, int line, boolean initialized, byte kind) {
        this.type = type;
        this.typeString = typeString;
        this.declarationLine = line;
        this.initialized = initialized;
        this.kind = kind;
        usageLines = new ArrayList<>();
        initializationLines = new ArrayList<>();
    }

    /**
     * Konštruktor, v ktorom nastavujeme základné hodnoty
     *
     * @param type dátovy typ premennej, resp. návratová hodnota
     *
     * @param typeString dátový typ premennej (String)
     *
     * @param line riadok deklarácie
     *
     * @param kind typ (premenná, pole, funkcia, parameter)
     */
    public Record(short type, String typeString, int line, byte kind) {
        this.type = type;
        this.typeString = typeString;
        this.declarationLine = line;
        this.kind = kind;
        usageLines = new ArrayList<>();
        initializationLines = new ArrayList<>();
    }

    /**
     * Konštruktor, v ktorom nastavujeme základné hodnoty
     *
     * @param type dátovy typ premennej, resp. návratová hodnota
     *
     * @param typeString dátový typ premennej (String)
     *
     * @param line riadok deklarácie
     *
     * @param initialized informácie, či premenná je inicializovaná
     */
    public Record(short type, String typeString, int line, boolean initialized) {
        this.type = type;
        this.typeString = typeString;
        this.declarationLine = line;
        this.initialized = initialized;
        usageLines = new ArrayList<>();
        initializationLines = new ArrayList<>();
    }

    /**
     * Metóda na zistenie hodnoty dátového typu.
     *
     * @return dátový typ
     */
    public short getType() {
        return type;
    }

    /**
     * Metóda na nastavenie dátového typu.
     *
     * @param type dátový typ premennej, resp. návratová hodnota funkcie
     */
    public void setType(short type) {
        this.type = type;
    }

    /**
     * Metóda na zistenie riadku deklarácie.
     *
     * @return riadok deklarácie
     */
    public int getDeclarationLine() {
        return declarationLine;
    }

    /**
     * Metóda na nastavenie riadku deklarácie
     *
     * @param declarationLine riadok deklarácie
     */
    public void setDeclarationLine(int declarationLine) {
        this.declarationLine = declarationLine;
    }

    /**
     * Metóda na zistenie toho, či premenná je inicializovaná
     *
     * @return true, ak premenná je inicializovaná, inak false
     */
    public boolean getInitialized() {
        return initialized;
    }

    /**
     * Metóda na nastavenie informácie o tom, či premenná je inicializovaná.
     *
     * @param initialized true, ak premenná je inicializovaná, inak false
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Metóda na zistenie druhu.
     * @return druh (premenná, pole, funkcia, parameter, ...)
     */
    public byte getKind() {
        return kind;
    }

    /**
     * Metóda na zistenie druhu.
     * @param kind druh (premenná, pole, funkcia, parameter, ...)
     */
    public void setKind(byte kind) {
        this.kind = kind;
    }

    /**
     * Metóda na zistenie všetkých paramterov funkcie.
     *
     * @return pole všetkých parametrov funkcie, inak null
     */
    public ArrayList<String> getParameters() {
        return parameters;
    }

    /**
     * Metóda na nastavenie paramterov funkcie
     *
     * @param parameters pole paramterov funkcie, ak nie sú žiadne, tak null
     */
    public void setParameters(ArrayList<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Metóda na zistenie veľkosti poľa.
     *
     * @return veľkosť poľa
     */
    public int getSize() {
        return size;
    }

    /**
     * Metóda na nastavenie veľkosti poľa.
     *
     * @param size veľkosť poľa
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Metóda na zistenie dátového typu (String).
     *
     * @return dátový typ (String)
     */
    public String getTypeString() {
        return typeString;
    }

    /**
     * Metóda na nastavenie dátového typu (String).
     *
     * @param typeString dátový typ (String)
     */
    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    /**
     * Metóda na zistenie zoznamu riadkov využitia premennej.
     *
     * @return zoznam riadkov využitia premennej
     */
    public ArrayList<Integer> getUsageLines() {
        return usageLines;
    }

    /**
     * Metóda na nastavenie riadkov využitia premennej.
     *
     * @param usageLines zoznam riadkov využitia premennej.
     */
    public void setUsageLines(ArrayList<Integer> usageLines) {
        this.usageLines = usageLines;
    }

    /**
     * Metóda na zistenie zoznamu riadkov inicializácie premennej.
     *
     * @return zoznam riadkov inicializácie premennej.
     */
    public ArrayList<Integer> getInitializationLines() {
        return initializationLines;
    }

    /**
     * Metóda na nastavenie zoznamu riadkov inicializácie premennej.
     *
     * @param initializationLines zoznam riadkov inicializácie premennej
     */
    public void setInitializationLines(ArrayList<Integer> initializationLines) {
        this.initializationLines = initializationLines;
    }

    /**
     * Metóda na pridanie riadku do zoznamu riadkov využitia premennej.
     *
     * @param line riadok využitia
     */
    public void addUsageLine(int line) {
        int length = usageLines.size();
        if (length == 0 || usageLines.get(length - 1) != line) {
            usageLines.add(line);
        }

    }

    /**
     * Metóda na pridanie riadku inicializácie premennej
     *
     * @param line riadok inicializácie
     */
    public void addInitializationLine(int line) {
        int length = initializationLines.size();
        if (length == 0 || initializationLines.get(length - 1) != line) {
            initializationLines.add(line);
        }
    }

    /**
     * Metóda pre konvertovanie informácií na reťazec.
     *
     * @return informácie konvertnované na reťazec.
     */
    @Override
    public String toString() {
        StringBuilder usageLinesString = new StringBuilder();
        for (int i : usageLines) {
            usageLinesString.append(i);
            usageLinesString.append(", ");
        }
        StringBuilder initializationLinesString = new StringBuilder();
        for (int i: initializationLines) {
            initializationLinesString.append(i);
            initializationLinesString.append(", ");
        }

        return "Typ (short): " + type +
                "\nTyp (String): " + typeString +
                "\nRiadok deklarácie: " + declarationLine +
                "\nInicializovaná: " + initialized +
                "\nRiadky využití: " + usageLinesString.toString() +
                "\nRiadky inicializácií: " + initializationLinesString.toString() +
                "\nKind: " + kind +
                "\nParametre: " + String.join(", ", parameters) +
                "\nVeľkosť poľa: " + size
                + "\n----";
    }
}