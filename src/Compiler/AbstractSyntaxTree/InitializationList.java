package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre zoznam inicializácií v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class InitializationList extends Node {
    /** Atribút expressions predstavuje zoznam inicializátorov. **/
    ArrayList<Node> expressions;

    /**
     * Konštruktor, ktorý vytvára triedu {@code InitializationList} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných do symbolickej tabuľky.
     *
     * @param exprs zoznam inicializátorov
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public InitializationList(ArrayList<Node> exprs, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expressions = exprs;
        setLine(line);

        for (Node node : expressions) {
            SymbolTableFiller.resolveUsage(node, table, errorDatabase, true, true);
        }
    }

    /**
     * Metóda pre pridanie yužitia premenných v rámci {@code InitializationList}, pre zadaný riadok.
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, na ktorom sa premenné využívajú
     */
    @Override
    public void resolveUsage(SymbolTable table, int line) {
        for (Node node: expressions) {
            SymbolTableFiller.resolveUsage(node, table, line);
            node.resolveUsage(table, line);
        }
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "InitializationList: ");
        if (expressions != null) {
            for (Node expr : expressions) {
                expr.traverse(indent + "    ");
            }
        }
    }

    /**
     * Metóda pre pridanie inicializátora do zoznamu inicializátorov.
     *
     * @param expr inicializátor
     */
    public void addExpression(Node expr) {
        expressions.add(expr);
    }

}
