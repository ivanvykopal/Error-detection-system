package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre do while cyklus v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class DoWhile extends Node {
    /** Atribút condition obahuje vrchol pre podmienkovú časť do while cyklu.**/
    Node condition;

    /** Atribút statement obsahuje vrchol pre blok príkazov, ktoré sa vykonávajú v prípade splnenia podmienky.**/
    Node statement;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Assignment} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných v podmienkovej časti do symbolickej tabuľky.
     *
     * @param cond vrchol pre podmienku
     *
     * @param stmt vrchol pre blok príkazov
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public DoWhile(Node cond, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.condition = cond;
        this.statement = stmt;
        setLine(line);

        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true);
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
        SymbolTableFiller.resolveUsage(condition, table, line);
        condition.resolveUsage(table, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "DoWhile: ");
        if (condition != null) condition.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
