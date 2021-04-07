package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre definovanie vlastného typu cez typedef.
 *
 * @author Ivan Vykopal
 *
 * @see DeclarationNode
 * @see Node
 */
public final class Typedef extends DeclarationNode {
    /** Atribút storage predtavuje zoznam storage kvalifikátorov (typedef, extern static, ...). **/
    ArrayList<String> storage;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Typedef} a inicilizuje jej atribúty.
     *
     * @param name ázov deklarovanej premennej
     *
     * @param quals zoznam kvalifikátorov (volatile, const, ...)
     *
     * @param storage zoznam storage špecifikátorov (extern, register, auto, ...)
     *
     * @param type vrchol pre typ deklarácie
     *
     * @param line riadok využitia
     */
    public Typedef(String name, ArrayList<String> quals, ArrayList<String> storage, Node type, int line) {
        super(name, quals, type, line);
        this.storage = storage;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Typedef:");
        if (name != null) System.out.println(indent + name);
        if (qualifiers != null) {
            System.out.print(indent);
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
        if (storage != null) {
            System.out.print(indent);
            for (String stor : storage) {
                System.out.print(stor + ", ");
            }
            System.out.print("\n");
        }
        if (type != null) type.traverse(indent + "    ");
    }
}