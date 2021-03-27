package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre switch v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Switch extends Node {
    /** Atribút condition predstavuje switch podmienku. **/
    Node condition;

    /** Atribút statement predstavuje vrchol pre blok príkazov. **/
    Node statement;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Switch} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných z podmienky do symbolickej tabuľky.
     *
     * @param cond switch podmienka.
     *
     * @param stmt vrchol pre blok príkazov
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public Switch(Node cond, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.condition = cond;
        this.statement = stmt;
        setLine(line);

        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true, true);
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
        System.out.println(indent + "Switch: ");
        if (condition != null) condition.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
