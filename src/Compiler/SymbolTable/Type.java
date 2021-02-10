package Compiler.SymbolTable;

/**
 * Trieda, ktorá určuje dátový typ premennej alebo návratovú hodnotu funkcie
 */
public class Type {

    // Character
    public static byte CHAR = 0;
    public static byte SIGNEDCHAR = 1;
    public static byte UNSIGNEDCHAR = 2;

    // Integer types
    public static byte SHORT = 3;
    public static byte SIGNEDSHORT = 4;
    public static byte UNSIGNEDSHORT = 5;
    public static byte INT = 6;
    public static byte SIGNED = 7;
    public static byte SIGNEDINT = 8;
    public static byte UNSIGNED = 9;
    public static byte UNSIGNEDINT = 10;
    public static byte SHORTINT = 11;
    public static byte SIGNEDSHORTINT = 12;
    public static byte UNSIGNEDSHORTINT = 13;
    public static byte LONG = 14;
    public static byte SIGNEDLONG = 15;
    public static byte UNSIGNEDLONG = 16;
    public static byte LONGINT = 17;
    public static byte SIGNEDLONGINT = 18;
    public static byte UNSIGNEDLONGINT = 19;
    public static byte LONGLONG = 20;
    public static byte LONGLONGINT = 21;
    public static byte SIGNEDLONGLONG = 22;
    public static byte SIGNEDLONGLONGINT = 23;
    public static byte UNSIGNEDLONGLONG = 24;
    public static byte UNSIGNEDLONGLONGINT = 25;

    // Real numbers
    public static byte FLOAT = 26;
    public static byte DOUBLE = 27;
    public static byte LONGDOUBLE = 28;

    public static byte UNION = 29;
    public static byte STRUCT = 30;
    public static byte ENUM = 31;

    public static byte VOID = 32;
}