package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre enum v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Enum extends Node {
    /** Atribút name obsahuje názov pre enum.**/
    String name;

    /** Atribút values obsahuje vrchol pre hodnoty v enum.**/
    Node values;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Enum} a inicilizuje jej atribúty.
     *
     * @param name názov pre enum
     *
     * @param values vrchol pre hodnoty v enum
     *
     * @param line riadok využitia
     */
    public Enum(String name, Node values, int line) {
        this.name = name;
        this.values = values;
        setLine(line);
    }

    /**
     * Metóda pre zistenie názov enum.
     *
     * @return názov enum
     */
    public String getName() {
        return name;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Enum: ");
        if (name != null) System.out.println(indent + name);
        if (values != null) values.traverse(indent + "    ");
    }

    /**
     * Metóda na zistenie, či ide o triedu {@code Enum}, {@code Struct} alebo {@code Union}
     *
     * @return true, ak ide o {@code Enum}, {@code Struct} alebo {@code Union}
     *         false, ak nejde o {@code Enum}, {@code Struct} alebo {@code Union}
     */
    @Override
    public boolean isEnumStructUnion() {
        return true;
    }

}
