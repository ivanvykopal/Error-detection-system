package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

/**
 * Trieda predstavujúca vrchol pre prístup do poľa v jazyku C.
 *
 * @author Ivan Vykopal
 *
 * @see Node
 */
public final class ArrayReference extends Node {
    /** Atribút obsahujúci názov premennej. **/
    Node name;

    /** Atribút obsahujúci index prístupu do poľa. **/
    Node index;

    /**
     * Konštruktor, ktorý vytvára triedu {@code ArrayReference} a inicilizuje jej atribúty.
     *
     * <p> V rámci konštruktora sa zároveň vykonáva typová kontrola prístupu do poľa.
     * V prípade, ak sa nevyskytla typová nezhoda, kontroluje sa hodnota prístupu do poľa v rámci kontroly prístupu mimo
     * pamäť.
     *
     * <p> Následne sa pridáva využitie premenných v atribúte index do symbolickej tabuľky.
     *
     * @param name vrchol pre názov premennej
     *
     * @param index vrchol pre index prístupu do poľa
     *
     * @param line riadok volania
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public ArrayReference(Node name, Node index, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.name = name;
        this.index = index;
        setLine(line);

        if (!typeCheck(table)) {
            System.out.println("Sémantická chyba na riadku " + line + "!");
            //TODO: pridať zisťovanie chyby do errorDatabase
        } else {
            findAccessError(name, index, table, errorDatabase);
        }

        SymbolTableFiller.resolveUsage(index, table, errorDatabase, true);
    }

    /**
     * Metóda pre kontrolu prístupu mimo pamäť.
     *
     * <p> V rámci kontroly sa testuje prístup do poľa len v prípade, ak ide o konštantu.
     *
     * @param nodeName vrchol pre názov premennej
     *
     * @param nodeIndex vrchol pre index prístupu do poľa
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    private void findAccessError(Node nodeName, Node nodeIndex, SymbolTable table, ErrorDatabase errorDatabase) {
        int arrayIndex;
        if (nodeIndex instanceof Constant) {
            try {
                arrayIndex = Integer.parseInt(((Constant) nodeIndex).getValue());
            } catch (NumberFormatException e) {
                return;
            }
        } else {
            return;
        }

        Node id = nodeName;

        while (!(id instanceof Identifier)) {
            id = id.getNameNode();
        }

        Record record = table.lookup(((Identifier) id).getName());
        if (record != null) {
            if (record.getSize() != 0 && arrayIndex >= record.getSize()) {
                System.out.println("Prístup mimo pamäť na riadku " + line + "!");
                errorDatabase.addErrorMessage(nodeName.getLine(), Error.getError("E-RP-06"), "E-RP-06");
            }
        }
    }

    /**
     * Metóda pre typovú kontrolu vrcholu indexu prístupu do poľa.
     *
     * @param table symbolická tabuľka
     *
     * @return true, ak nie je typová nezhoda
     *         false, ak je typová nezhoda
     */
    private boolean typeCheck(SymbolTable table) {
        short type = findTypeCategory(index, table);
        return type <= Type.UNSIGNEDLONGLONGINT;

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
     * Metóda pre pridanie yužitia premenných v rámci {@code ArrayReference}, pre zadaný riadok.
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, na ktorom sa premenné využívajú
     */
    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(index, table, line);
        index.resolveUsage(table, line);
    }

    /**
     * Metóda, ktorá vracia vrchol pre názov premennej.
     *
     * @return vrchol pre názov premennej.
     */
    @Override
    public Node getNameNode() {
        return name;
    }

    /**
     * Metóda pre prechádzanie jednotlivých vrcholov stromu (Abstract syntax tree).
     *
     * @param indent odriadkovanie pre správne formátovanie
     */
    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ArrayReference: ");
        if (name != null) name.traverse(indent + "    ");
        if (index != null) index.traverse(indent + "    ");
    }

}
