package Compiler.AbstractSyntaxTree;

import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

public class UnaryOperator extends Node {
    Node expression;
    String operator;
    short typeCategory;

    public UnaryOperator(Node expr, String op, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expression = expr;
        this.operator = op;
        setLine(line);

        resolveUsage(expression, table, errorDatabase);

        if (!typeCheck(table)) {
            //TODO: Sémantická chyba
            System.out.println("Sémantická chyba na riadku " + line + "!");
        }
    }

    private boolean typeCheck(SymbolTable table) {
        short type = findTypeCategory(expression, table);
        if (type == -1) {
            typeCategory = -1;
            return false;
        }
        if (type == -2) {
            typeCategory = -2;                                                              //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            return true;
        }
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
        if (type < 29) {
            typeCategory = type;
            return true;
        } else if ((type % 50) < 29) {
            typeCategory = type;
            return true;
        } else {
            typeCategory = -1;
            return false;
        }
    }

    private short findTypeCategory(Node left, SymbolTable table) {
        if (left instanceof  Identifier) {
            //nájsť v symbolickej tabuľke
            Record record = table.lookup(((Identifier) left).getName());
            if (record == null) {
                return -2;                                                                  //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (left instanceof Constant) {
            return findType(((Constant) left).getTypeSpecifier());
        } else if (left instanceof FunctionCall) {
            Node id = left.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -2;                                                                 //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (left instanceof ArrayReference) {
            Node id = left.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof StructReference) {
            Node id = left.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof UnaryOperator) {
            return ((UnaryOperator) left).getTypeCategory();
        } else if (left instanceof Cast) {
            Node tail = left.getType();
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
            return findType(type);
        } else {
            return -1;
        }
    }

    public short getTypeCategory() {
        return typeCategory;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "UnaryOperator");
        if (operator != null) System.out.println(indent + operator);
        if (expression != null) expression.traverse(indent + "    ");
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEnumStructUnion() {
        return false;
    }

    @Override
    public boolean isTypeDeclaration() {
        return false;
    }

    @Override
    public Node getType() {
        return null;
    }

    @Override
    public void addType(Node type) {

    }

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}