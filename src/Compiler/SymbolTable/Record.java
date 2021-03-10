package Compiler.SymbolTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Trieda obsahujúca informácie o identifikátore.
 * - dátový typ, riadok deklarovania, hodnota pri deklarácii, prvé využitie a typ (premenná, pole, ...)
 * Pre pole sa pridáva zoznam parametrov a veľkosť poľa.
 */
public class Record implements Serializable {
    private short type;
    private String typeString;
    private int declarationLine;
    private boolean initialized = false;
    private ArrayList<Integer> usageLines;
    private ArrayList<Integer> initializationLines;
    private byte kind;

    // atribúty pre pole
    private ArrayList<String> parameters = new ArrayList<>();
    private int size = 0;

    /**
     * Konštruktor, v ktorom nastavujeme základné hodnoty.
     * @param type - dátovy typ premennej, resp. návratová hodnota
     * @param typeString - dátový typ premennej (String)
     * @param line - riadok deklarácie
     * @param initialized - hodnota pri deklarácii
     * @param kind - typ (premenná, pole, funkcia, parameter)
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
     * @param type - dátovy typ premennej, resp. návratová hodnota
     * @param typeString - dátový typ premennej (String)
     * @param line - riadok deklarácie
     * @param kind  - typ (premenná, pole, funkcia, parameter)
     */
    public Record(short type, String typeString, int line, byte kind) {
        this.type = type;
        this.typeString = typeString;
        this.declarationLine = line;
        this.kind = kind;
        usageLines = new ArrayList<>();
        initializationLines = new ArrayList<>();
    }

    public Record(short type, String typeString, int line, boolean initialized) {
        this.type = type;
        this.typeString = typeString;
        this.declarationLine = line;
        this.initialized = initialized;
        usageLines = new ArrayList<>();
        initializationLines = new ArrayList<>();
    }

    /**
     * Funkcia na zistenie hodnoty dátového typu.
     * @return dátový typ
     */
    public short getType() {
        return type;
    }

    /**
     * Funkcia na nastavenie dátového typu.
     * @param type - dátový typ premennej, resp. návratová hodnota funkcie
     */
    public void setType(short type) {
        this.type = type;
    }

    /**
     * Funkcia na zistenie riadku deklarácie.
     * @return riadok deklarácie
     */
    public int getDeclarationLine() {
        return declarationLine;
    }

    /**
     * Funkcia na nastavenie riadku deklarácie
     * @param declarationLine - riadok deklarácie
     */
    public void setDeclarationLine(int declarationLine) {
        this.declarationLine = declarationLine;
    }

    /**
     * Funkcia na zistenie hodnoty deklarácie
     * @return hodnota deklarácie
     */
    public boolean getInitialized() {
        return initialized;
    }

    /**
     * Funkcia na nastavenie hodnoty deklarácie.
     * @param initialized - hodnota deklarácie
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /*/**
     * Funkcia na zistenie riadku prvého využitia premennej alebo funkcie.
     * @return riadok prvého využitia
     /
    public int getFirstUsage() {
        return firstUsage;
    }

    /**
     * Funkcia na nastavenie riadku prvého využitia premennej alebo funkcie.
     * @param firstUsage - riadok prvého využitia
     *
    public void setFirstUsage(int firstUsage) {
        this.firstUsage = firstUsage;
    }
    */
    /**
     * Funkcia na zistenie typu.
     * @return typ (premenná, pole, funkcia, parameter)
     */
    public byte getKind() {
        return kind;
    }

    /**
     * Funkcia na zistenie typu.
     * @param kind - typ (premenná, pole, funkcia, parameter)
     */
    public void setKind(byte kind) {
        this.kind = kind;
    }

    /**
     * Funkcia na zistenie všetkých paramterov funkcie.
     * @return pole všetkých parametrov funkcie, inak null
     */
    public ArrayList<String> getParameters() {
        return parameters;
    }

    /**
     * Funkcia na nastavenie paramterov funkcie
     * @param parameters - pole paramterov funkcie, ak nie sú žiadne, tak null
     */
    public void setParameters(ArrayList<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Funkcia na zistenie veľkosti poľa.
     * @return veľkosť poľa
     */
    public int getSize() {
        return size;
    }

    /**
     * Funkcia na nastavenie veľkosti poľa.
     * @param size - veľkosť poľa
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Funkcia na zistenie dátového typu (String).
     * @return dátový typ (String)
     */
    public String getTypeString() {
        return typeString;
    }

    /**
     * Funkcia na nastavenie dátového typu (String).
     * @param typeString dátový typ (String)
     */
    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    /**
     *
     * @return
     */
    public ArrayList<Integer> getUsageLines() {
        return usageLines;
    }

    /**
     *
     * @param usageLines
     */
    public void setUsageLines(ArrayList<Integer> usageLines) {
        this.usageLines = usageLines;
    }

    /**
     *
     * @return
     */
    public ArrayList<Integer> getInitializationLines() {
        return initializationLines;
    }

    /**
     *
     * @param initializationLines
     */
    public void setInitializationLines(ArrayList<Integer> initializationLines) {
        this.initializationLines = initializationLines;
    }

    /**
     *
     * @param line
     */
    public void addUsageLine(int line) {
        usageLines.add(line);
    }

    /**
     *
     * @param line
     */
    public void addInitializationLine(int line) {
        initializationLines.add(line);
    }


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