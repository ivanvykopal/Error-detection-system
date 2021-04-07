package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol pre prístup do štruktúry v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class StructReference extends Node {
    /** Atribút name predstavuje vrchol pre názov štruktúry.**/
    Node name;

    /** Atribút type predstavuje typ prístupu (. alebo {@code ->}). **/
    String type;

    /** Atribút field predstavuje názov premennej zo štruktúry. **/
    Node field;

    /**
     *
     * Konštruktor, ktorý vytvára triedu {@code StructReference} a inicilizuje jej atribúty.
     *
     * @param name vrchol pre názov štruktúry
     *
     * @param type typ prístupu
     *
     * @param field názov premennej zo štruktúry
     *
     * @param line riadok využitia
     */
    public StructReference(Node name, String type, Node field, int line) {
        this.name = name;
        this.type = type;
        this.field = field;
        setLine(line);
    }

    /**
     * Metóda na zistenie vrcholu názvu.
     *
     * @return vrchol názvu.
     */
    @Override
    public Node getNameNode() {
        return field;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "StructReference: ");
        if (name != null) name.traverse(indent + "    ");
        if (type != null) System.out.println(indent + type);
        if (field != null) field.traverse(indent + "    ");
    }

}
