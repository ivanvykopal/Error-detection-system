package Compiler.Parser;

import java.util.ArrayList;

public class Leaf extends Node {
    private byte tag;
    private String value;
    private int line;

    /**
     * Konštruktor na nastavenie triedy tokenu, hodnoty a riadku.
     * @param tag trieda tokenu (numerická hodnota)
     * @param value hodnota tokenu
     * @param line riadok, na ktorom sa token vyskytol
     */
    public Leaf(byte tag, String value, int line) {
        this.tag = tag;
        this.value = value;
        this.line = line;
    }

    /**
     * Funkcia na zistenie triedy tokenu.
     * @return trieda tokenu (numerická hodnota)
     */
    public byte getTag() {
        return tag;
    }

    /**
     * Funkcia na nastavenie triedy tokenu.
     * @param tag trieda tokenu (numerická hodnota)
     */
    public void setTag(byte tag) {
        this.tag = tag;
    }

    /**
     * Funkcia na zistenie hodnoty tokenu
     * @return hodnota tokenu
     */
    public String getValue() {
        return value;
    }

    /**
     * Funkcia na nastavenie hodnoty tokenu.
     * @param value hodnota tokenu
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Funkcia na zistenie riadku výskytu tokenu.
     * @return riadok, na ktorom sa token vyskytol
     */
    public int getLine() {
        return line;
    }

    /**
     * Funkcia na nastavenie riadku výskytu tokenu.
     * @param line riadok, na ktorom sa token vyskytol
     */
    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public ArrayList<Node> getChilds() {
        return null;
    }

    @Override
    public void printData(String indent) {
        System.out.println(indent + tag + ", " + value + ", " + line);
    }
}
