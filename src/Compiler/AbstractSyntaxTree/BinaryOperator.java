package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

/**
 * Trieda predstavujúca vrchol pre binárny operátor v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class BinaryOperator extends Node {
    /** Atribút left predstavuje ľavú časť binárneho operátora. **/
    Node left;

    /** Atribút ritgh predstavuje pravú časť binárneho operátora. **/
    Node right;

    /** Atribút operator obsahuje informáciu o operátore. **/
    String operator;

    /** Atribút typeCatageory obsahuje informáciu o celkovom type binárneho operátora. **/
    short typeCategory;

    /**
     * Konštruktor, ktorý vytvára triedu {@code BinaryOperator} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie premenných do symbolickej tabuľky..
     *
     * <p> Následne sa vykonáva typová kontrola pravej a ľavej časti binárneho operátora. V prípade chyby sa zisťuje, či
     * na ľavej alebo pravej strane sa nachádza volanie funkcie, pre ktorú je špeciálny typ chyby.
     *
     * @param left vrchol, ktorý sa nachádza v ľavej časti priradenia
     *
     * @param op operátor
     *
     * @param right vrchol, ktorý sa nachádza v pravej časti priradenia
     *
     * @param line riadok využitia
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public BinaryOperator(Node left, String op, Node right, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.left = left;
        this.operator = op;
        this.right = right;
        setLine(line);

        SymbolTableFiller.resolveUsage(left, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(right, table, errorDatabase, true);

        if (!typeCheck(table)) {
            if (left instanceof FunctionCall || right instanceof FunctionCall) {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("L-SmA-03"), "L-SmA-03");
            } else {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("E-SmA-01"), "E-SmA-01");
            }
        }
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
        short var1 = findTypeCategory(left, table);
        short var2 = findTypeCategory(right, table);

        if (var1 == -2 || var2 == -2) {
            typeCategory = Type.CHAR;
            return true;
        }

        if (var1 == -1 || var2 == -1) {
            typeCategory = -1;
            return false;
        }

        short type = maxType(var1, var2);
        if (type == -1) {
            typeCategory = -1;
            return false;
        }
        switch (operator) {
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "==":
            case "!=":
                typeCategory = Type.INT;
                return true;
            case "&&":
            case "||":
                if (type < Type.UNSIGNEDLONGLONGINT) {
                    typeCategory = type;
                    return true;
                } else {
                    typeCategory = -1;
                    return false;
                }
            default:
                typeCategory = type;
                return true;
        }
    }

    /**
     * Metóda pre zistenie maximálneho typu.
     *
     * @param var1 typ ľavej časti binárneho operátora
     *
     * @param var2 typ pravej časti binárneho operátora
     *
     * @return maximálny typ binárneho operátora
     */
    private short maxType(short var1, short var2) {
        if (var1 == var2) {
            return var1;
        }
        if (var1 == Type.VOID || var2 == Type.VOID) {
            return -1;
        }
        if (var1 < 50 && var2 < 50 && var1 >= Type.UNION && var2 >= Type.UNION) {
            return -1;
        }
        if (var1 < 50 && var2 < 50 && var1 < Type.UNION && var2 < Type.UNION) {
            return (short) Math.max(var1, var2);
        }
        if (var1 > 50 && var2 > 50 && (var1 % 50) < Type.UNION && (var2 % 50) < Type.UNION) {
            return (short) Math.max(var1, var2);
        }
        return -1;
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
                return -2;
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
                return -2;
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
        } else  {
            return -1;
        }
    }

    /**
     * Metóda pre zistenie celkového typu binárneho operátora.
     *
     * @return celkový typ binárneho operátora
     */
    public short getTypeCategory() {
        return typeCategory;
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
        SymbolTableFiller.resolveUsage(left, table, line);
        SymbolTableFiller.resolveUsage(right, table, line);
        left.resolveUsage(table, line);
        right.resolveUsage(table, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "BinaryOperator: ");
        if (left != null) left.traverse(indent + "    ");
        if (operator != null) System.out.println(indent + operator);
        if (right != null) right.traverse(indent + "    ");
    }


}
