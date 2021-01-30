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

    public Record() {

    }
}