package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol so všetkými informáciami o type.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class TypeNode extends Node {
    /** Atribút types predstavuje zoznam typov (int, char, ...). **/
    ArrayList<Node> types;

    /** Atribút storage predstavuje zoznam storage kvalifikátorov (typedef, extern, ...). **/
    ArrayList<String> storage;

    /** Atribút qualifiers predstavuje zoznam kvalifikátorov (volatile, const). **/
    ArrayList<String> qualifiers;

    /**
     * Konštruktor, ktorý vytvára triedu {@code TypeNode} a inicilizuje jej atribúty.
     *
     * @param types zoznam typov (int, char, ...)
     *
     * @param storage zoznam storage kvalifikátorov (typedef, extern, ...)
     *
     * @param quals zoznam kvalifikátorov (volatile, const)
     */
    public TypeNode(ArrayList<Node> types, ArrayList<String> storage, ArrayList<String> quals) {
        this.types = types;
        this.storage = storage;
        this.qualifiers = quals;
    }

    /**
     * Metóda na pridanie typu do zoznamu typov.
     *
     * @param type typ
     */
    @Override
    public void addType(Node type) {
        types.add(type);
    }

    /**
     * Metóda na pridanie storage kvalifikátoru do zoznamu storage kvalifikátorov.
     *
     * @param storage storage kvalifikátoru
     */
    public void addStorage(String storage) {
        this.storage.add(storage);
    }

    /**
     * Metóda na pridanie kvalifikátora do zoznamu kvalifikátorov.
     *
     * @param qualifier kvalifikátor
     */
    public void addQualifier(String qualifier) {
        qualifiers.add(qualifier);
    }

    /**
     * Metóda na zistenie zoznamu typov.
     *
     * @return zoznam typov
     */
    public ArrayList<Node> getTypes() {
        return types;
    }

    /**
     * Metóda na zistenie zoznamu storage kvalifikátorov.
     *
     * @return zoznam storage kvalifikátorov
     */
    public ArrayList<String> getStorage() {
        return storage;
    }

    /**
     * Metóda na zistenie zoznamu kvalifikátorov.
     *
     * @return zoznam kvalifikátorov
     */
    public ArrayList<String> getQualifiers() {
        return qualifiers;
    }

    /**
     * Metóda na zistenie typu na základe indexu.
     *
     * @param index index v zozname typov
     *
     * @return typ na základe indexu
     */
    public Node getType(int index) {
        return types.get(index);
    }

    /**
     * Metóda na zistenie posledného typu.
     *
     * @return typ na poslednej pozícii
     */
    public Node getLastType() {
        return types.get(types.size() - 1);
    }

    /**
     * Metóda na odstránenie posledného typu zo zoznamu typov.
     */
    public void removeLastType() {
        types.remove(types.size() - 1);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Type: ");
        if (types != null) {
            for (Node type : types) {
                type.traverse(indent + "    ");
            }
        }
        if (storage != null) {
            System.out.print(indent);
            for (String stor : storage) {
                System.out.print(stor + ", ");
            }
            System.out.print("\n");
        }
        if (qualifiers != null) {
            System.out.print(indent);
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
    }

}
