package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

public class ArrayReference extends Node {
    Node name;
    Node index;

    public ArrayReference(Node name, Node index, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.name = name;
        this.index = index;
        setLine(line);

        if (!typeCheck(table)) {
            System.out.println("Sémantická chyba na riadku " + line + "!");
        } else {
            findAccessError(name, index, table, errorDatabase);
        }

        SymbolTableFiller.resolveUsage(index, table, errorDatabase, true);
    }

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

    private boolean typeCheck(SymbolTable table) {
        short type = findTypeCategory(index, table);
        return type <= Type.UNSIGNEDLONGLONGINT;

    }

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
            return TypeChecker.findType(((Constant) node).getTypeSpecifier() + " ");
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
            return TypeChecker.findType(type);
        } else if (node instanceof TernaryOperator) {
            return ((TernaryOperator) node).getTypeCategory();
        } else {
            return -1;
        }
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(index, table, line);
        index.resolveUsage(table, line);
    }

    @Override
    public Node getNameNode() {
        return name;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "ArrayReference: ");
        if (name != null) name.traverse(indent + "    ");
        if (index != null) index.traverse(indent + "    ");
    }

}
