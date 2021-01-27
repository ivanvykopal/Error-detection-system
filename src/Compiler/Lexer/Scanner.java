package Compiler.Lexer;

import java.util.HashMap;

/**
 * Trieda, ktorá spracováva vstup a mení ho na tokeny.
 */
public class Scanner {
    private String file;
    private HashMap<String, Byte> keywords = new HashMap<>();
    private char peek = ' ';
    private int position = 0;

    public static int line = 1;

    /**
     * Konštruktor, ktorý načíta vstupný súbor a zároveň naplní HashMap kľúčovými slovami,
     * pre ľahšiu kontrolu.
     * @param file - cesta k súboru
     */
    public Scanner(String file) {
        this.file = file;

        addKeywords();
    }

    /**
     * Funkcia, ktorá pridá klúčové slová do HashMap.
     */
    private void addKeywords() {
        keywords.put("auto", Tag.AUTO);
        keywords.put("break", Tag.BREAK);
        keywords.put("case", Tag.CASE);
        keywords.put("char", Tag.CHAR);
        keywords.put("const", Tag.CONST);
        keywords.put("continue", Tag.CONTINUE);
        keywords.put("default", Tag.DEFAULT);
        keywords.put("do", Tag.DO);
        keywords.put("double", Tag.DOUBLE);
        keywords.put("else", Tag.ELSE);
        keywords.put("enum", Tag.ENUM);
        keywords.put("extern", Tag.EXTERN);
        keywords.put("float", Tag.FLOAT);
        keywords.put("for", Tag.FOR);
        keywords.put("goto", Tag.GOTO);
        keywords.put("if", Tag.IF);
        keywords.put("int", Tag.INT);
        keywords.put("long", Tag.LONG);
        keywords.put("Register", Tag.REGISTER);
        keywords.put("return", Tag.RETURN);
        keywords.put("short", Tag.SHORT);
        keywords.put("signed", Tag.SIGNED);
        keywords.put("sizeof", Tag.SIZEOF);
        keywords.put("static", Tag.STATIC);
        keywords.put("struct", Tag.STRUCT);
        keywords.put("switch", Tag.SWITCH);
        keywords.put("typedef", Tag.TYPEDEF);
        keywords.put("union", Tag.UNION);
        keywords.put("unsigned", Tag.UNSIGNED);
        keywords.put("void", Tag.VOID);
        keywords.put("volatile", Tag.VOLATILE);
        keywords.put("while", Tag.WHILE);
    }
}