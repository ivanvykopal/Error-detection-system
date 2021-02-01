package Compiler.Lexer;

/**
 * Trieda obsahujúca všetky potrebné druhy tried pre tokeny.
 */
public class Tag {
    //keywords
    public static final byte AUTO = 0;                // auto
    public static final byte BREAK = 1;               // break
    public static final byte CASE = 2;                // case
    public static final byte CHAR = 3;                // char
    public static final byte CONST = 4;               // const
    public static final byte CONTINUE = 5;            // continue
    public static final byte DEFAULT = 6;             // default
    public static final byte DO = 7;                  // do
    public static final byte DOUBLE = 8;              // double
    public static final byte ELSE = 9;                // else
    public static final byte ENUM = 10;               // enum
    public static final byte EXTERN = 11;             // extern
    public static final byte FLOAT = 12;              // float
    public static final byte FOR = 13;                // for
    public static final byte GOTO = 14;               // goto
    public static final byte IF = 15;                 // if
    public static final byte INT = 16;                // int
    public static final byte LONG = 17;               // long
    public static final byte REGISTER = 18;           // register
    public static final byte RETURN = 19;             // return
    public static final byte SHORT = 20;              // short
    public static final byte SIGNED = 21;             // signed
    public static final byte SIZEOF = 22;             // sizeof
    public static final byte STATIC = 23;             // static
    public static final byte STRUCT = 24;             // struct
    public static final byte SWITCH = 25;             // switch
    public static final byte TYPEDEF = 26;            // typdef
    public static final byte UNION = 27;              // union
    public static final byte UNSIGNED = 28;           // unsigned
    public static final byte VOID = 29;               // void
    public static final byte VOLATILE = 30;           // volatile
    public static final byte WHILE = 31;              // while

    public static final byte IDENTIFIER = 32;         // identifier
    public static final byte NUMBER = 33;             // int number
    public static final byte REAL = 34;               // real number
    public static final byte STRING = 35;             // string
    public static final byte CHARACTER = 36;          // character

    // operators
    public static final byte OP_ARITHMETIC = 37;      // + - * / % ++ --
    public static final byte OP_RELATIONAL = 38;      // == != > < >= <=
    public static final byte OP_LOGICAL = 39;         // && || !
    public static final byte OP_BITWISE = 40;         // << >> ~ & ^ |
    public static final byte OP_ASSIGNMENT = 41;      // = += -= *= /= %= <<= >>= &= ^= |=
    public static final byte OP_MISCELLANEOUS = 42;   // & *

    // symbols
    public static final byte LEFT_PARENTHESES = 43;   // [
    public static final byte RIGHT_PARENTHESES = 44;  // ]
    public static final byte LEFT_BRACKETS = 45;      // (
    public static final byte RIGHT_BRACKETS = 46;     // )
    public static final byte LEFT_BRACES = 47;        // {
    public static final byte RIGHT_BRACES = 48;       // }
    public static final byte COMMA = 49;              // ,
    public static final byte SEMICOLON = 50;          // ;
    public static final byte COLON = 51;              // :
    public static final byte HASHTAG = 52;            // #
    public static final byte DOT = 53;                // .
    public static final byte QMARK = 54;              // ?
    public static final byte ARROW = 55;              // ->
}