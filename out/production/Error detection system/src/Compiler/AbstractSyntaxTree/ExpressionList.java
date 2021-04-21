package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import java.util.ArrayList;

/**
 * Trieda predstavujúca vrchol pre priradenie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class ExpressionList extends Node {
    /** Atribút expressions obsahuje zoznam výrazov. **/
    ArrayList<Node> expressions;

    /**
     * Konštruktor, ktorý vytvára triedu {@code ExpressionList} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných z výrazov do symbolickej tabuľky.
     *
     * @param exprs zoznam výrazov
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public ExpressionList(ArrayList<Node> exprs, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expressions = exprs;
        setLine(line);

        for (Node node : expressions) {
            SymbolTableFiller.resolveUsage(node, table, errorDatabase, false, true);
        }
    }

    /**
     * Metóda pre pridanie yužitia premenných v rámci {@code Assignment}, pre zadaný riadok.
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, na ktorom sa premenné využívajú
     */
    @Override
    public void resolveUsage(SymbolTable table, int line) {
        for (Node node : expressions) {
            SymbolTableFiller.resolveUsage(node, table, line);
            node.resolveUsage(table, line);
        }
    }

    /**
     * Metóda pre zistenie zoznamu výrazov.
     *
     * @return zoznam výrazov
     */
    public ArrayList<Node> getExpressions() {
        return expressions;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ExpressionList: ");
        if (expressions != null) {
            for (Node expr : expressions) {
                expr.traverse(indent + "    ");
            }
        }
    }

}
