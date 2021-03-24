package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

/**
 * Trieda predstavujúca vrchol pre unárny operátor v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class UnaryOperator extends Node {
    /** Atribút expression predstavuje výraz v unárnom operátori. **/
    Node expression;

    /** Atribút operator predstavuje unárny operátor. **/
    String operator;

    /** Atribút typeCategory predstavuje typ unárneho operátora. **/
    short typeCategory;

    /**
     * Konštruktor, ktorý vytvára triedu {@code UnaryOperator} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie a inicializácie premenných pri priradení do symbolickej tabuľky.
     *
     * <p> Následne sa vykonáva typová kontrola výrazu. V prípade chyby sa zisťuje, či výraz nie je volanie funkcie,
     * pre ktorú je špeciálny typ chyby.
     *
     * @param expr výraz
     *
     * @param op operátor
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public UnaryOperator(Node expr, String op, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expression = expr;
        this.operator = op;
        setLine(line);

        if (operator.equals("&")) {
            SymbolTableFiller.resolveUsage(expression, table, errorDatabase, false);
        } else {
            if (operator.equals("++") || operator.equals("--")) {
                SymbolTableFiller.resolveInitialization(expression, table, errorDatabase);
            }
            SymbolTableFiller.resolveUsage(expression, table, errorDatabase, true);
        }

        if (!typeCheck(table)) {
            if (expression instanceof FunctionCall) {
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
        if (operator.equals("++") || operator.equals("--")) {
            SymbolTableFiller.resolveInitialization(expression, table, line);
        }
        SymbolTableFiller.resolveUsage(expression, table, line);
        expression.resolveUsage(table, line);
    }

    /**
     * Metóda pre typovú kontrolu priradenia.
     *
     * @param table symbolická tabuľka
     *
     * @return true, ak nie je typová nezhoda
     *         false, ak je typová nezhoda
     */
    private boolean typeCheck(SymbolTable table) {
        short type = TypeChecker.findTypeCategory(expression, table);
        switch (operator) {
            case "&":
                if (expression instanceof Constant && type != Type.STRING) {
                    typeCategory = -1;
                    return false;
                }
                typeCategory = Type.UNSIGNEDINT;
                return true;
            case "*":
                if (type > 50) {
                    type -= 50;
                }
                break;
            case "sizeof":
                typeCategory = Type.UNSIGNEDINT;
                return true;
        }
        if (type == -1) {
            typeCategory = -1;
            return false;
        }
        if (type == -2) {
            typeCategory = -2;                                                              //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            return true;
        }
        if (type < Type.UNION) {
            typeCategory = type;
            return true;
        } else if (type > 50 && (type % 50) < Type.UNION) {
            typeCategory = type;
            return true;
        } else {
            typeCategory = -1;
            return false;
        }
    }

    /**
     * Metóda na zistenie vrcholu výrazu.
     *
     * @return vrchol výrazu
     */
    public Node getExpression() {
        return expression;
    }

    /**
     * Metóda na zistenie unárneho operátora.
     *
     * @return operátor
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Metóda pre zistenie typu unárneho operátora.
     *
     * @return typ unárneho operátora
     */
    public short getTypeCategory() {
        return typeCategory;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "UnaryOperator");
        if (operator != null) System.out.println(indent + operator);
        if (expression != null) expression.traverse(indent + "    ");
    }

}