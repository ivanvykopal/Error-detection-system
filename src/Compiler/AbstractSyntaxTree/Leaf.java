package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre list stromu. Obsahuje informácie samotného tokenu.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public class Leaf extends Node {
    /** Atribút tag predstavuje druh tokenu z triedy Tag. **/
    private byte tag;

    /** Atribút value predstavuje hodnotu tokenu. **/
    private String value;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Leaf} a inicilizuje jej atribúty.
     *
     * @param tag trieda tokenu (numerická hodnota)
     *
     * @param value hodnota tokenu
     *
     * @param line riadok, na ktorom sa token vyskytol
     */
    public Leaf(byte tag, String value, int line) {
        this.tag = tag;
        this.value = value;
        setLine(line);
    }

    /**
     * Metóda na zistenie triedy tokenu.
     *
     * @return trieda tokenu (numerická hodnota)
     */
    public byte getTag() {
        return tag;
    }

    /**
     * Metóda na nastavenie triedy tokenu.
     *
     * @param tag trieda tokenu (numerická hodnota)
     */
    public void setTag(byte tag) {
        this.tag = tag;
    }

    /**
     * Metóda na zistenie hodnoty tokenu.
     *
     * @return hodnota tokenu
     */
    public String getValue() {
        return value;
    }

    /**
     * Metóda na nastavenie hodnoty tokenu.
     *
     * @param value hodnota tokenu
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Metóda na zistenie riadku výskytu tokenu.
     *
     * @return riadok, na ktorom sa token vyskytol
     */
    public int getLine() {
        return line;
    }

    /**
     * Metóda na nastavenie riadku výskytu tokenu.
     *
     * @param line riadok, na ktorom sa token vyskytol
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {

    }

}
