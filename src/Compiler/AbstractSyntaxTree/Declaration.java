package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre deklaráciu v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see DeclarationNode
 * @see Node
 */
public final class Declaration extends DeclarationNode {
    /** Atribút storage obsahuje zoznam storage špecifikátorov (extern, register, auto, ...)**/
    ArrayList<String> storage;

    /** Atribút initValues obsahuje vrhol pre inicializačnú hodnotu. **/
    Node initValues;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Declaration} a inicilizuje jej atribúty.
     *
     * @param name názov deklarovanej premennej
     *
     * @param quals zoznam kvalifikátorov (volatile, const, ...)
     *
     * @param storage zoznam storage špecifikátorov (extern, register, auto, ...)
     *
     * @param type  vrchol pre typ deklarácie
     *
     * @param init  vrchol pre inicializačnú hodnotu
     *
     * @param line riadok využitia
     */
    public Declaration(String name, ArrayList<String> quals, ArrayList<String> storage, Node type, Node init, int line) {
        super(name, quals, type, line);
        this.storage = storage;
        this.initValues = init;
        setLine(line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Declaration: ");
        if (name != null) System.out.print(indent + name);
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
        if (initValues != null) initValues.traverse(indent + "    ");
    }

}