package Compiler.AbstractSyntaxTree;

import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre struct v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Struct extends Node {
    /** Atribút name predstavuje názov štruktúry. **/
    String name;

    /** Atribút declarations predstavuje zoznam deklarácií v štruktúre. **/
    ArrayList<Node> declarations;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Struct} a inicilizuje jej atribúty.
     *
     * @param name názov štruktpry
     *
     * @param decls zoznam deklarácií v štruktúre
     *
     * @param line riadok využitia
     */
    public Struct(String name, ArrayList<Node> decls, int line) {
        this.name = name;
        this.declarations = decls;
        setLine(line);
    }

    /**
     * Metóda na zistenie názvu štruktúry.
     *
     * @return názov štruktúry
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
        System.out.println(indent + "Struct: ");
        if (name != null) System.out.println(indent + name);
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
