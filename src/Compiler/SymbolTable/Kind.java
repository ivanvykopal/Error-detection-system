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
    public static byte ARRAY_PARAMETER = 4;
    public static byte TYPEDEF_NAME = 5;
    public static byte ENUMERATION_CONSTANT = 6;
    public static byte STRUCT_PARAMETER = 7;
    public static byte STRUCT_ARRAY_PARAMETER = 8;
}