package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.*;

/**
 * Trieda predstavujúca vrchol pre priradenie v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class Assignment extends Node {
    /** Atribút left obsahuje vrchol, ktorý sa nachádza v ľavej časti priradenia. **/
    Node left;

    /** Atribút right obsahuje vrchol, ktorý sa nachádza v pravej časti priradenia. **/
    Node right;

    /** Atribút operator obsahuje informáciu o využitom operátori. **/
    String operator;

    /**
     * Konštruktor, ktorý vytvára triedu {@code Assignment} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň pridáva využitie a inicializácie premenných pri priradení do symbolickej
     * tabuľky.
     *
     * <p> Následne sa vykonáva typová kontrola pravej a ľavej časti priradenia. V prípade chyby sa zisťuje, či
     * na pravej strane sa nachádza volanie funkcie, pre ktorú je špeciálny typ chyby. V prípade, ak sa nenašla typová
     * nezhoda kontroluje sa priradenie dynamického smerníka do statického.
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
    public Assignment(Node left, String op, Node right, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.left = left;
        this.operator = op;
        this.right = right;
        setLine(line);

        SymbolTableFiller.resolveInitialization(left, table, errorDatabase);
        if(!operator.equals("=")) {
            SymbolTableFiller.resolveUsage(left, table, errorDatabase, true);
        }
        SymbolTableFiller.resolveUsage(right, table, errorDatabase, true);

        if (!typeCheck(table)) {
            if (right instanceof FunctionCall) {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("L-SmA-03"), "L-SmA-03");
            } else {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("E-SmA-01"), "E-SmA-01");
            }
        } else if (left instanceof Identifier) {
            Record record = table.lookup(((Identifier) left).getName());
            if (record != null && (record.getKind() == Kind.ARRAY || record.getKind() == Kind.ARRAY_PARAMETER ||
                    record.getKind() == Kind.STRUCT_ARRAY_PARAMETER) && findTypeCategory(right, table) > 50) {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("E-RP-08"), "E-RP-08");
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
        if (var2 == -2) {
            return true;
        }

        if ((left instanceof ArrayDeclaration) && (var1 == Type.CHAR || var1 == Type.SIGNEDCHAR || var1 == Type.UNSIGNEDCHAR)
                && var2 == Type.STRING) {
            return true;
        }

        if (var2 == Type.VOID) {
            return false;
        }
        if (var1 == var2) {
            if (left instanceof Struct) {
                Record leftType = table.lookup(((Struct) left).getName());
                Record rightType = table.lookup(((Struct) right).getName());
                if (leftType != null && rightType != null && leftType.getTypeString().equals(rightType.getTypeString())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (left instanceof Union) {
                Record leftType = table.lookup(((Union) left).getName());
                Record rightType = table.lookup(((Union) right).getName());
                if (leftType != null && rightType != null && leftType.getTypeString().equals(rightType.getTypeString())) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }
        if (var1 < 50 && var2 < 50 && var1 >= Type.UNION && var2 >= Type.UNION) {
            return false;
        }
        if ( var1 > 50 && var2 > 50 && (var1 % 50) >= Type.UNION && (var2 % 50) >= Type.UNION) {
            return false;
        }
        return true;
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
        } else {
            return -1;
        }
    }

    /**
     * Metóda pre zistenie typu ľavej časti priradenia.
     *
     * @param table symbolická tabuľka
     *
     * @return typ ľavej časti priradenia
     */
    public short getLeftType(SymbolTable table) {
        return findTypeCategory(left, table);
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
        SymbolTableFiller.resolveInitialization(left, table, line);
        if (!operator.equals("=")) {
            SymbolTableFiller.resolveUsage(left, table, line);
        }
        SymbolTableFiller.resolveUsage(right, table, line);
        right.resolveUsage(table, line);
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Assignmnent: ");
        if (left != null) left.traverse(indent + "    ");
        if (operator != null) System.out.println(indent + operator);
        if (right != null) right.traverse(indent + "    ");
    }

}
