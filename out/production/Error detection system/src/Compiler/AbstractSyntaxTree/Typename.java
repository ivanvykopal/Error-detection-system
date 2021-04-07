package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre "type_name" z gramatiky.
 *
 * @author Ivan Vykopal
 *
 * @see DeclarationNode
 * @see Node
 */
public final class Typename extends DeclarationNode {

    /**
     * Konštruktor, ktorý vytvára triedu {@code Typename} a inicilizuje jej atribúty.
     *
     * @param name názov premennej
     *
     * @param quals zoznam kvalifikátorov (volatile, const, ...)
     *
     * @param type vrchol pre typ deklarovanej premennej
     *
     * @param line riadok využitia
     */
    public Typename(String name, ArrayList<String> quals, Node type, int line) {
        super(name, quals, type, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Typename: ");
        if (name != null) System.out.println(indent + name);
        if (qualifiers != null) {
            System.out.print(indent);
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
        if (type != null) type.traverse(indent + "    ");
    }
}
