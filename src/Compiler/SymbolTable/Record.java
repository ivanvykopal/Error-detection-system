package Compiler.SymbolTable;

import java.util.ArrayList;

/**
 * Trieda obsahujúca informácie o identifikátore.
 * - dátový typ, riadok deklarovania, hodnota pri deklarácii, prvé využitie a typ (premenná, pole, ...)
 * Pre pole sa pridáva zoznam parametrov a veľkosť poľa.
 */
public class Record {
    private byte type;
    private int declarationLine;
    private String declarationValue;
    private int firstUsage;
    private byte kind;

    // atribúty pre pole
    private ArrayList<String> parameters;
    private int size;

    /**
     * Konštruktor, v ktorom nastavujeme základné hodnoty
     * @param type - dátovy typ premennej, resp. návratová hodnota
     * @param line - riadok deklarácie
     * @param value - hodnota pri deklarácii
     * @param kind - typ (premenná, pole, funkcia, parameter)
     */
    public Record(byte type, int line, String value, byte kind) {
        this.type = type;
        this.declarationLine = line;
        this.declarationValue = value;
        this.kind = kind;
    }

    /**
     *
     * @param type
     * @param line
     * @param kind
     */
    public Record(byte type, int line, byte kind) {
        this.type = type;
        this.declarationLine = line;
        this.kind = kind;
    }

    /**
     * Funkcia na zistenie hodnoty dátového typu.
     * @return dátový typ
     */
    public byte getType() {
        return type;
    }

    /**
     * Funkcia na nastavenie dátového typu.
     * @param type - dátový typ premennej, resp. návratová hodnota funkcie
     */
    public void setType(byte type) {
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
    public String getDeclarationValue() {
        return declarationValue;
    }

    /**
     * Funkcia na nastavenie hodnoty deklarácie.
     * @param declarationValue - hodnota deklarácie
     */
    public void setDeclarationValue(String declarationValue) {
        this.declarationValue = declarationValue;
    }

    /**
     * Funkcia na zistenie riadku prvého využitia premennej alebo funkcie.
     * @return riadok prvého využitia
     */
    public int getFirstUsage() {
        return firstUsage;
    }

    /**
     * Funkcia na nastavenie riadku prvého využitia premennej alebo funkcie.
     * @param firstUsage - riadok prvého využitia
     */
    public void setFirstUsage(int firstUsage) {
        this.firstUsage = firstUsage;
    }

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

}