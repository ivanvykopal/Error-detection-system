package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre inicializácie hodnôt v rámci { }.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public class NamedInitializer extends Node {
    /** Atribút names predstavuje zoznam mien. Zoznam z dôvodu výskytu hierarchického mena (e.g. .id.id). **/
    ArrayList<Node> names;

    /** Atribút expression predstavuje výraz, ktorým inicializujeme atribút. **/
    Node expression;

    /**
     * Konštruktor, ktorý vytvára triedu {@code InitializationList} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných do symbolickej tabuľky.
     *
     * @param names zoznam mien
     *
     * @param expr výraz určený pre inicializáciu
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public NamedInitializer(ArrayList<Node> names, Node expr, SymbolTable table, ErrorDatabase errorDatabase) {
        this.names = names;
        this.expression = expr;

        SymbolTableFiller.resolveUsage(expression, table, errorDatabase, true, true);
    }

    /**
     * Metóda pre pridanie yužitia premenných v rámci {@code NamedInitializer}, pre zadaný riadok.
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, na ktorom sa premenné využívajú
     */
    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(expression, table, line);
        expression.resolveUsage(table, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "NamedInitializer: ");
        if (names != null) {
            for (Node name : names) {
                name.traverse(indent + "    ");
            }
        }
        if (expression != null) expression.traverse(indent + "    ");
    }

}
