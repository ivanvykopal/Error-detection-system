package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre konštantu v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Constant extends Node {
    /** Atribút type obsahuje typ konštanty. **/
    String type;

    /** Atribút value obsahuje hodnotu konštanty. **/
    String value;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Constant} a inicilizuje jej atribúty.
     *
     * @param type typ konštanty
     *
     * @param value hodnota konštanty
     *
     * @param line riadok využitia
     */
    public Constant(String type, String value, int line) {
        this.type = type;
        this.value = value;
        setLine(line);
    }

    /**
     * Metóda pre zistenie typu konštanty.
     *
     * @return typ konštanty (v podobe reťazca)
     */
    public String getTypeSpecifier() {
        return type;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Constant: ");
        if (type != null) System.out.println(indent + type);
        if (value != null) System.out.println(indent + value);
    }

    /**
     * Metóda pre zistenie hodnoty konštanty.
     *
     * @return hodnota konštanty
     */
    public String getValue() {
        return value;
    }

}
