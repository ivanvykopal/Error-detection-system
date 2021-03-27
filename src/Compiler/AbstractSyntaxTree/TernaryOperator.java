package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

/**
 * Trieda predstavujúca vrchol pre ternárny operátor v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class TernaryOperator extends Node {
    /** Atribút condition predstavuje podmienku ternárneho operátora. **/
    Node condition;

    /** Atribút truePart predstavuje vetvu za splnenia podmienky. **/
    Node truePart;

    /** Atribút falsePart predstavuje vetvu za nesplnenia podmienky. **/
    Node falsePart;

    /** Atribút typeCategory predstavuje spoločný typ truePart a falsePart.
     * Potrebný v prípade priraďaovania.
     * **/
    short typeCategory;

    /**
     * Konštruktor, ktorý vytvára triedu {@code TernaryOperator} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných do symbolickej tabuľky.
     *
     * <p> Následne sa vykonáva typová kontrola truePart a falsePart vetiev. V prípade typovej nezhody sa zisťuje, či sa
     * v truePart alebo falsePart vetve nachádza volanie funkcie, pre ktorú je špeciálny typ chyby.
     *
     * @param cond podmienka ternárneho operátora.
     *
     * @param truePart vetva pre splnenú podmienku
     *
     * @param falsePart vetva pre nesplnenú podmienku
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public TernaryOperator(Node cond, Node truePart, Node falsePart, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.condition = cond;
        this.truePart = truePart;
        this.falsePart = falsePart;
        setLine(line);

        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true, true);
        SymbolTableFiller.resolveUsage(truePart, table, errorDatabase, true, true);
        SymbolTableFiller.resolveUsage(falsePart, table, errorDatabase, true, true);

        if (!typeCheck(table)) {
            if (truePart instanceof FunctionCall || falsePart instanceof FunctionCall) {
                errorDatabase.addErrorMessage(line, Error.getError("L-SmA-03"), "L-SmA-03");
            } else {
                errorDatabase.addErrorMessage(line, Error.getError("E-SmA-01"), "E-SmA-01");
            }
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
        SymbolTableFiller.resolveUsage(condition, table, line);
        condition.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(truePart, table, line);
        truePart.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(falsePart, table, line);
        falsePart.resolveUsage(table, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "TernaryOperator:");
        if (condition != null) condition.traverse(indent + "    ");
        if (truePart != null) truePart.traverse(indent + "    ");
        if (falsePart != null) falsePart.traverse(indent + "    ");
    }

    /**
     * Metóda pre typovú kontrolu ternárneho operátora.
     *
     * @param table symbolická tabuľka
     *
     * @return true, ak nie je typová nezhoda
     *         false, ak je typová nezhoda
     */
    private boolean typeCheck(SymbolTable table) {
        short var1 = TypeChecker.findTypeCategory(truePart, table);
        short var2 = TypeChecker.findTypeCategory(falsePart, table);

        if (var1 == -2 && var2 >= 0) {
            typeCategory = var2;
            return true;
        }
        if (var2 == -2 && var1 >= 0) {
            typeCategory = var1;
            return true;
        }
        if (var1 == -2 && var2 == -2) {
            typeCategory = -2;
            return true;
        }

        if (var2 == Type.VOID) {
            typeCategory = -1;
            return false;
        }
        if (var1 == var2) {
            typeCategory = var1;
            return true;
        }
        var1 = (var1 >= 50) ? (short) (var1 % 50) : var1;
        var2 = (var2 >= 50) ? (short) (var2 % 50) : var2;

        if (var1 >= Type.UNION && var2 >= Type.UNION) {
            typeCategory = -1;
            return false;
        }
        if (var1 >= Type.UNION && var2 < Type.UNION || var2 >= Type.UNION && var1 < Type.UNION) {
            typeCategory = -1;
            return false;
        }

        typeCategory = (byte) Math.max(var1, var2);
        return true;
    }

    /**
     * Metóda pre zistenie celkového typu ternárneho operátora.
     *
     * @return celkový typ ternárneho operátora
     */
    public short getTypeCategory() {
        return typeCategory;
    }

}
