package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre case v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Case extends Node {
    /** Atribút constant obsahuje konštantný výraz pre porovnávanie v switch. **/
    Node constant;

    /** Atribút statement obsahuje blok príkazov pre vykonanie v rpípade zhody konštantného výrazu. **/
    Node statement;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Case} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných do symbolickej tabuľky.
     *
     * @param cont vrchol pre konštantný výraz
     *
     * @param stmt vrchol pre blok príkazov
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public Case(Node cont, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.constant = cont;
        this.statement = stmt;
        setLine(line);

        SymbolTableFiller.resolveUsage(constant, table, errorDatabase, true);
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
        SymbolTableFiller.resolveUsage(constant, table, line);
        constant.resolveUsage(table, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Case: ");
        if (constant != null) constant.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
