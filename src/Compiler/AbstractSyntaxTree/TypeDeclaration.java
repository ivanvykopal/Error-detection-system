package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre identifikátor splou s jeho typom.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class TypeDeclaration extends Node {
    /** Atribút declname predstavuje názov identifikátora. **/
    String declname;

    /** Atribút qualifiers predstavuje zoznam kvalifikátorov (vloatile, const) **/
    ArrayList<String> qualifiers;

    /** Atribút type predstavuje vrchol pre typ identifikátora. **/
    Node type;

    /**
     * Konštruktor, ktorý vytvára triedu {@code TypeDeclaration} a inicilizuje jej atribúty.
     *
     * @param declname názov identifikátora
     *
     * @param quals zoznam kvalifikátorov (volatile, const)
     *
     * @param type vrchol pre typ identifikátora
     */
    public TypeDeclaration(String declname, ArrayList<String> quals, Node type) {
        this.declname = declname;
        this.qualifiers = quals;
        this.type = type;
    }

    /**
     * Konštruktor, ktorý vytvára triedu {@code TypeDeclaration} a inicilizuje jej atribúty.
     *
     * @param declname názov identifikátora
     *
     * @param quals zoznam kvalifikátorov (volatile, const)
     *
     * @param type vrchol pre typ identifikátora
     *
     * @param line riadok využitia
     */
    public TypeDeclaration(String declname, ArrayList<String> quals, Node type, int line) {
        this(declname, quals, type);
        setLine(line);
    }

    /**
     * Metóda na zistenie názvu identifikátora.
     *
     * @return názov identifikátora
     */
    public String getDeclname() {
        return declname;
    }

    /**
     * Metóda pre nastavenie názvu identifikátora.
     *
     * @param declname názov identifikátora
     */
    public void addDeclname(String declname) {
        this.declname = declname;
    }

    /**
     * Metóda pre nastavenie zoznamu kvalifikátorov.
     *
     * @param quals zoznam kvalifikátorov
     */
    public void addQualifiers(ArrayList<String> quals) {
        this.qualifiers = quals;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "TypeDeclaration: ");
        if (declname != null) System.out.println(indent + declname);
        if (qualifiers != null) {
            System.out.print(indent);
            for (String qual : qualifiers) {
                System.out.print(qual + ", ");
            }
            System.out.print("\n");
        }
        if (type != null) type.traverse(indent + "    ");
    }

    /**
     * Metóda na zistenie vrcholu pre typ identifikátora.
     *
     * @return vrchol pre typ identifikátora
     */
    @Override
    public Node getType() {
        return type;
    }

    /**
     * Metóda na nastavenie typu identifikátora.
     *
     * @param type typ identifikátora
     */
    @Override
    public void addType(Node type) {
        this.type = type;
    }

}
