package Compiler.SymbolTable;

/**
 * Trieda, ktorá určuje o aký typ identifikátora ide.
 * Typy: premenná, pole, funkcia, parameter
 */
public class Kind {
    public static byte VARIABLE = 0;
    public static byte ARRAY = 1;
    public static byte FUNCTION = 2;
    public static byte PARAMETER = 3;
}