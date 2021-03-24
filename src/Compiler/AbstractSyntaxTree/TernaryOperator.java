package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
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

        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(truePart, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(falsePart, table, errorDatabase, true);

        if (!typeCheck(table)) {
            if (truePart instanceof FunctionCall || falsePart instanceof FunctionCall) {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("L-SmA-03"), "L-SmA-03");
            } else {
                System.out.println("Sémantická chyba na riadku " + line + "!");
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
        short var1 = findTypeCategory(truePart, table);
        short var2 = findTypeCategory(falsePart, table);

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
        if (var1 < 50 && var2 < 50 && var1 >= Type.UNION && var2 >= Type.UNION) {
            typeCategory = -1;
            return false;
        }
        if (var1 > 50 && var2 > 50 && (var1 % 50) < Type.UNION && (var2 % 50) < Type.UNION) {
            typeCategory = (byte) Math.max(var1, var2);
            return true;
        }
        return false;
    }

    /**
     * Metóda pre nájdenie kategórie typu pre zadaný vrchol.
     *
     * @param node vrchol, ktorého typ zisťujeme
     *
     * @param table symbolická tabuľka
     *
     * @return typ daného vrcholu
     */
    private short findTypeCategory(Node node, SymbolTable table) {
        if (node instanceof BinaryOperator) {
            return ((BinaryOperator) node).getTypeCategory();
        } else if (node instanceof Identifier) {
            //nájsť v symbolickej tabuľke
            Record record = table.lookup(((Identifier) node).getName());
            if (record == null) {
                return -2;                                                      //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (node instanceof Constant) {
            return TypeChecker.findType(((Constant) node).getTypeSpecifier() + " ", null, table);
        } else if (node instanceof FunctionCall) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -2;                                                      //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (node instanceof ArrayReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (node instanceof StructReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (node instanceof UnaryOperator) {
            return ((UnaryOperator) node).getTypeCategory();
        } else if (node instanceof Cast) {
            Node tail = node.getType();
            String type = "";
            boolean pointer = false;

            while (!(tail instanceof IdentifierType)) {
                if (tail.isEnumStructUnion()) {
                    if (tail instanceof Enum) {
                        type = "enum ";
                    } else if (tail instanceof Struct) {
                        type = "struct ";
                    } else {
                        type = "union ";
                    }
                    break;
                }
                if (tail instanceof PointerDeclaration) {
                    pointer = true;
                }
                tail = tail.getType();
            }

            if (pointer) {
                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) tail).getNames()) + " * ";
                } else {
                    type += "* ";
                }
            } else {
                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) tail).getNames()) + " ";
                }
            }

            //spojí všetky typy do stringu a konvertuje ich na byte
            return TypeChecker.findType(type, tail, table);
        } else if (node instanceof TernaryOperator) {
            return ((TernaryOperator) node).getTypeCategory();
        } else if (node instanceof Assignment) {
            return ((Assignment) node).getLeftType(table);
        } else {
            return -1;
        }
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
