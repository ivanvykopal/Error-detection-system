package Compiler.SymbolTable;

/**
 * Trieda, ktorá určuje dátový typ premennej alebo návratovú hodnotu funkcie
 */
public class Type {

    // Character 1B
    public static short CHAR = 0;
    public static short SIGNEDCHAR = 1;
    public static short UNSIGNEDCHAR = 2;

    // Integer types 2B
    public static short SHORT = 3;
    public static short SIGNEDSHORT = 4;
    public static short UNSIGNEDSHORT = 5;
    public static short SHORTINT = 6;
    public static short SIGNEDSHORTINT = 7;
    public static short UNSIGNEDSHORTINT = 8;

    //Integer types 4B
    public static short INT = 9;
    public static short SIGNED = 10;
    public static short SIGNEDINT = 11;
    public static short UNSIGNED = 12;
    public static short UNSIGNEDINT = 13;
    public static short LONG = 14;
    public static short SIGNEDLONG = 15;
    public static short UNSIGNEDLONG = 16;
    public static short LONGINT = 17;
    public static short SIGNEDLONGINT = 18;
    public static short UNSIGNEDLONGINT = 19;

    //Integer types 8B
    public static short LONGLONG = 20;
    public static short LONGLONGINT = 21;
    public static short SIGNEDLONGLONG = 22;
    public static short SIGNEDLONGLONGINT = 23;
    public static short UNSIGNEDLONGLONG = 24;
    public static short UNSIGNEDLONGLONGINT = 25;

    // Real numbers
    public static short FLOAT = 26;
    public static short DOUBLE = 27;
    public static short LONGDOUBLE = 28;

    public static short UNION = 29;
    public static short STRUCT = 30;
    public static short ENUM = 31;

    public static short VOID = 32;
    public static short TYPEDEF_TYPE = 33;

    public static short STRING = 34;
    public static short FILE = 35;
}