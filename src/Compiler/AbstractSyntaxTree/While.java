package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre while cyklus v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class While extends Node {
    /** Atribút condition predstavuje podmienku while cyklu. **/
    Node condition;

    /** Atribút statement predstavuje telo while cyklu. **/
    Node statement;

    /**
     * Konštruktor, ktorý vytvára triedu {@code While} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných pri priradení do symbolickej
     * tabuľky.
     *
     * @param cond podmienka pre while cyklus
     *
     * @param stmt telo while cyklu
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public While(Node cond, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
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
        System.out.println(indent + "While:");
        if (condition != null) condition.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
