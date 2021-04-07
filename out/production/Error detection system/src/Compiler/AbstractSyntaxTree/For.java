package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre for cyklus v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class For extends Node {
    /** Atribút initializer obsahuje vrchol pre inicializáciu vo for cykle.**/
    Node initializer;

    /** Atribút condition obsahuje vrchol pre podmienkovu časť for cykla. **/
    Node condition;

    /** Atribút next obsahuje vrchol pre prikazy, ktoré sa vykonávajú každým prechodom cyklu.**/
    Node next;

    /** Atribút statement obsahuje blok príkazov v tele for cyklu. **/
    Node statement;

    /**
     * Konštruktor, ktorý vytvára triedu {@code For} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie a deklarácia premenných do symbolickej tabuľky.
     *
     * @param init vrchol pre inicializáciu
     *
     * @param cond vrchol pre podmienku
     *
     * @param next vrchol pre príkazy, vykonávané pri prechode for cyklu.
     *
     * @param stmt vrchol pre blok príkazov (telo for cyklus)
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public For(Node init, Node cond, Node next, Node stmt, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.initializer = init;
        this.condition = cond;
        this.next = next;
        this.statement = stmt;
        setLine(line);

        SymbolTableFiller.resolveUsage(initializer, table, errorDatabase, true, true);
        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true, true);
        SymbolTableFiller.resolveUsage(next, table, errorDatabase, true, true);
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
        SymbolTableFiller.resolveUsage(initializer, table, line);
        initializer.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(condition, table, line);
        condition.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(next, table, line);
        next.resolveUsage(table, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "For: ");
        if (initializer != null) initializer.traverse(indent + "    ");
        if (condition != null) condition.traverse(indent + "    ");
        if (next != null) next.traverse(indent + "    ");
        if (statement != null) statement.traverse(indent + "    ");
    }

}
