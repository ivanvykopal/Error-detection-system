package Compiler.SymbolTable;

import java.util.ArrayList;

/**
 * Trieda obsahujúca informácie o identifikátore.
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

    public Record(byte type, int line, String value, byte kind) {
        this.type = type;
        this.declarationLine = line;
        this.declarationValue = value;
        this.kind = kind;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getDeclarationLine() {
        return declarationLine;
    }

    public void setDeclarationLine(int declarationLine) {
        this.declarationLine = declarationLine;
    }

    public String getDeclarationValue() {
        return declarationValue;
    }

    public void setDeclarationValue(String declarationValue) {
        this.declarationValue = declarationValue;
    }

    public int getFirstUsage() {
        return firstUsage;
    }

    public void setFirstUsage(int firstUsage) {
        this.firstUsage = firstUsage;
    }

    public byte getKind() {
        return kind;
    }

    public void setKind(byte kind) {
        this.kind = kind;
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<String> parameters) {
        this.parameters = parameters;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}