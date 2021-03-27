package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;

/**
 * Trieda predstavujúca vrchol pre pretypovanie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Cast extends Node {
    /** Atribút type obsahuje vrchol typ, na ktorý pretypovávame daný výraz. **/
    Node type;

    /** Atribút expression obsahuje vrchol pre výraz, ktorý chceme pretypovať. **/
    Node expression;

    /**
     *  Konštruktor, ktorý vytvára triedu {@code Cast} a inicilizuje jej atribúty.
     *
     *  <p> V rámci konštruktora sa zároveň pridáva využitie premenných do symbolickej tabuľky.
     *
     * @param type vrchol pre typ, na ktorý pretypovávame
     *
     * @param expr vrchol pre výraz, ktorý chceme pretypovať
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public Cast(Node type, Node expr, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.type = type;
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
        System.out.println(indent + "Cast:");
        if (type != null) type.traverse(indent + "    ");
        if (expression != null) expression.traverse(indent + "    ");
    }

    /**
     * Metóda, ktorá vracia vrchol pre typ.
     *
     * @return vrchol pre typ
     */
    @Override
    public Node getType() {
        return type;
    }

    /**
     * Metóda prostredníctvom, ktorej pridávame typ.
     *
     * @param type vrchol pre typ
     */
    @Override
    public void addType(Node type) {
        this.type = type;
    }

}
