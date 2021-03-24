package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre if v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class If extends Node {
    /** Atribút condition predstavuje podmienku.**/
    Node condition;

    /** Atribút truePart predstavuje vetvu v prípade splnenia podmienky. **/
    Node truePart;

    /** Atribút faslePart predstavuje vetvu v prípade nesplnenia podmienky. **/
    Node falsePart;

    /**
     * Konštruktor, ktorý vytvára triedu {@code If} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných do symbolickej tabuľky.
     *
     * @param cond podmienka
     *
     * @param truePart vetva pre splenie podmienky
     *
     * @param falsePart vetva pre nesplnenie podmienky
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public If(Node cond, Node truePart, Node falsePart, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.condition = cond;
        this.truePart = truePart;
        this.falsePart = falsePart;
        setLine(line);

        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(truePart, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(falsePart, table, errorDatabase, true);
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
        System.out.println(indent + "If: ");
        if (condition != null) condition.traverse(indent + "    ");
        if (truePart != null) truePart.traverse(indent + "    ");
        if (falsePart != null) falsePart.traverse(indent + "    ");
    }

}
