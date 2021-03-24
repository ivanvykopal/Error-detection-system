package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca rodičovskú triedu pre {@code Declaration}, {@code Typedef} a {@code Typename}.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public class DeclarationNode extends Node {
    /** Atribút name obsahuje názov deklarovanej premennej.**/
    String name;

    /** Atribút qualifiers obsahuje zoznam kvalifikátorov (volatile, const, ...). **/
    ArrayList<String> qualifiers;

    /** Atribút type obsahuje vrchol pre typ deklarovanej premennej. **/
    Node type;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Assignment} a inicilizuje jej atribúty.
     *
     * @param name názov premennej
     *
     * @param quals zoznam kvalifikátorov (volatile, const, ...)
     *
     * @param type vrchol pre typ deklarovanej premennej
     *
     * @param line riadok využitia
     */
    public DeclarationNode(String name, ArrayList<String> quals, Node type, int line) {
        this.name = name;
        this.qualifiers = quals;
        this.type = type;
        setLine(line);
    }

    /**
     * Metóda pre zistenie názvu deklarovanej premennej.
     *
     * @return názov deklarovanej premennej
     */
    public String getName() {
        return name;
    }

    /**
     * Metóda pre nastavenie mena deklarovanej premennej
     *
     * @param name názov deklarovanej premennej
     */
    public void addName(String name) {
        this.name = name;
    }

    /**
     * Metóda pre zistenie zoznamu kvalifikátorov premennej.
     *
     * @return zoznam kvalifikátorov premennej
     */
    public ArrayList<String> getQualifiers() {
        return qualifiers;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
    }

    /**
     * Metóda, ktorá vracia vrchol pre typ deklarovanej premennej.
     *
     * @return vrchol pre typ deklarovanej premennej
     */
    @Override
    public Node getType() {
        return type;
    }

    /**
     * Metóda prostredníctvom, ktorej pridávame typ deklarovanej premennej.
     *
     * @param type vrchol pre typ deklarovanej premennej
     */
    @Override
    public void addType(Node type) {
        this.type = type;
    }

}
