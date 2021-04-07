package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre union v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public class Union extends Node {
    /** Atribút name predstavuje názov unionu. **/
    String name;

    /** Atribút declarations predstavuje zoznanm deklarácií v union. **/
    ArrayList<Node> declarations;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Union} a inicilizuje jej atribúty.
     *
     * @param name názov unionu
     *
     * @param decls zoznam deklarácií v union
     *
     * @param line riadok využitia
     */
    public Union(String name, ArrayList<Node> decls, int line) {
        this.name = name;
        this.declarations = decls;
        setLine(line);
    }

    /**
     * Metóda na zistenie názvu unionu.
     *
     * @return názov unionu.
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
        System.out.println(indent + "Union:");
        if (declarations != null) {
            for (Node decl : declarations) {
                decl.traverse(indent + "    ");
            }
        }
    }

    /**
     * Metóda na zistienie, či daný vrchol je trieda {@code Enum}, {@code Struct} alebo {@code Union}.
     *
     * @return true, ak ide o triedu {@code Enum}, {@code Struct} alebo {@code Union}
     *         false, ak nejde o triedu {@code Enum}, {@code Struct} alebo {@code Union}
     */
    @Override
    public boolean isEnumStructUnion() {
        return true;
    }

}
