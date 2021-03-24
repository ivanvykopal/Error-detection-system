package Compiler.AbstractSyntaxTree;

/**
 * Trieda predstavujúca vrchol v jazyku C.
 * Ide o pretypovanie bloku v rámci gramatiky: ( type_name ) { initializer_list }
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class CompoundLiteral extends Node {
    /** Atribút type obsahuje vrchol pre typ.**/
    Node type;

    /** Atribút values obsahuje vrchol pre "initializer_list".**/
    Node values;

    /**
     * Konštruktor, ktorý vytvára triedu {@code CompoundLiteral} a inicilizuje jej atribúty.
     *
     * @param type vrchol pre typ
     *
     * @param values vrchol pre hodnoty v "initializer_list"
     */
    public CompoundLiteral(Node type, Node values) {
        this.type = type;
        this.values = values;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "CompoundLiteral: ");
        if (type != null) type.traverse(indent + "    ");
        if (values != null) values.traverse(indent + "    ");
    }

    /**
     * Metóda, ktorá vracia vrchol pre typ.
     *
     * @return vrchol pre typ
     */
    @Override
    public Node getType() {
        return type;
    }

    /**
     * Metóda prostredníctvom, ktorej pridávame typ.
     *
     * @param type vrchol pre typ
     */
    @Override
    public void addType(Node type) {
        this.type = type;
    }

}
