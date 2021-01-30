package Compiler.Lexer;

/**
 * Trieda obsahujúca všetky potrebné druhy tried pre tokeny.
 */
public class Tag {
    //keywords
    public static byte AUTO = 0;                // auto
    public static byte BREAK = 1;               // break
    public static byte CASE = 2;                // case
    public static byte CHAR = 3;                // char
    public static byte CONST = 4;               // const
    public static byte CONTINUE = 5;            // continue
    public static byte DEFAULT = 6;             // default
    public static byte DO = 7;                  // do
    public static byte DOUBLE = 8;              // double
    public static byte ELSE = 9;                // else
    public static byte ENUM = 10;               // enum
    public static byte EXTERN = 11;             // extern
    public static byte FLOAT = 12;              // float
    public static byte FOR = 13;                // for
    public static byte GOTO = 14;               // goto
    public static byte IF = 15;                 // if
    public static byte INT = 16;                // int
    public static byte LONG = 17;               // long
    public static byte REGISTER = 18;           // register
    public static byte RETURN = 19;             // return
    public static byte SHORT = 20;              // short
    public static byte SIGNED = 21;             // signed
    public static byte SIZEOF = 22;             // sizeof
    public static byte STATIC = 23;             // static
    public static byte STRUCT = 24;             // struct
    public static byte SWITCH = 25;             // switch
    public static byte TYPEDEF = 26;            // typdef
    public static byte UNION = 27;              // union
    public static byte UNSIGNED = 28;           // unsigned
    public static byte VOID = 29;               // void
    public static byte VOLATILE = 30;           // volatile
    public static byte WHILE = 31;              // while

    public static byte IDENTIFIER = 32;         // identifier
    public static byte NUMBER = 33;             // int number
    public static byte REAL = 34;               // real number
    public static byte STRING = 35;             // string
    public static byte CHARACTER = 36;          // character

    // operators
    public static byte OP_ARITHMETIC = 37;      // + - * / % ++ --
    public static byte OP_RELATIONAL = 38;      // == != > < >= <=
    public static byte OP_LOGICAL = 39;         // && || !
    public static byte OP_BITWISE = 40;         // << >> ~ & ^ |
    public static byte OP_ASSIGNMENT = 41;      // = += -= *= /= %= <<= >>= &= ^= |=
    public static byte OP_MISCELLANEOUS = 42;   // & *

    // symbols
    public static byte LEFT_PARENTHESES = 43;   // [
    public static byte RIGHT_PARENTHESES = 44;  // ]
    public static byte LEFT_BRACKETS = 45;      // (
    public static byte RIGHT_BRACKETS = 46;     // )
    public static byte LEFT_BRACES = 47;        // {
    public static byte RIGHT_BRACES = 48;       // }
    public static byte COMMA = 49;              // ,
    public static byte SEMICOLON = 50;          // ;
    public static byte COLON = 51;              // :
    public static byte HASHTAG = 52;            // #
    public static byte DOT = 53;                // .
    public static byte QMARK = 54;              // ?
    public static byte ARROW = 55;              // ->
}