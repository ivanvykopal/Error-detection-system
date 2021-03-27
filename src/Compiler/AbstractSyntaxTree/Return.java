package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre return v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Return extends Node {
    /** Atribút expression predstavuje vrchol pre výraz. **/
    Node expression;

    /**
     *
     * Konštruktor, ktorý vytvára triedu {@code Return} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných vo výraze do symbolickej tabuľky.
     *
     * @param expr vrchol pre výraz
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public Return(Node expr, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expression = expr;
        setLine(line);

        SymbolTableFiller.resolveUsage(expression, table, errorDatabase, true, true);
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
        System.out.println(indent + "Return:");
        if (expression != null) expression.traverse(indent + "    ");
    }

}
