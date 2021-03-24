package Compiler.Lexer;

/**
 * Trieda obsahujúca štruktúru tokenu.
 * Token obsahuje triedu (numerická hodnota), hodnotu, číslo riadku.
 *
 * @author Ivan Vykopal
 */
public class Token {
    /** Atribút tag obsahuje triedu tokenu (numerickú hodnotu). **/
    public byte tag;

    /** Atribút value obsahuje hodnotu tokenu. **/
    public String value;

    /** Atribút line obsahuje číslo riadku, na ktorom sa token vyskytuje. **/
    public int line;

    /**
     * Konštruktor pre triedu Token.
     *
     * @param tag trieda tokenu (numerická hodnota)
     *
     * @param value hodnota tokenu
     *
     * @param line číslo riadku, na ktorom sa token vyskytol
     */
    public Token(byte tag, String value, int line) {
        this.tag = tag;
        this.value = value;
        this.line = line;
    }
}