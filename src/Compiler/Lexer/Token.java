package Compiler.Lexer;

/**
 * Trieda obsahujúca štruktúru tokenu.
 * Token obsahuje triedu (numerická hodnota), hodnotu, číslo riadku.
 */

public class Token {
    public byte tag;
    public String value;
    public int line;

    /**
     * Konštruktor pre triedu Token.
     * @param tag trieda tokenu (numerická hodnota)
     * @param value hodnota tokenu
     * @param line číslo riadku, na ktorom sa token vyskytol
     */
    public Token(byte tag, String value, int line) {
        this.tag = tag;
        this.value = value;
        this.line = line;
    }
}