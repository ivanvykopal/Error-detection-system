package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

public class UnaryOperator extends Node {
    Node expression;
    String operator;
    byte typeCategory;

    public UnaryOperator(Node expr, String op, SymbolTable table, int line) {
        this.expression = expr;
        this.operator = op;
        setLine(line);

        if (!typeCheck(table)) {
            //TODO: Sémantická chyba
            System.out.println("Sémantická chyba na riadku " + line + "!");
        }
    }

    private boolean typeCheck(SymbolTable table) {
        byte type = findTypeCategory(expression, table);
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
                //TODO: zatiaľ provizorne ak je len jeden *
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
        } else {
            typeCategory = -1;
            return false;
        }
    }

    private byte findTypeCategory(Node left, SymbolTable table) {
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
            Identifier id = (Identifier) ((FunctionCall) left).getName();
            Record record = table.lookup(id.getName());
            if (record == null) {
                return -2;                                                                 //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (left instanceof ArrayReference) {
            Identifier id = (Identifier) ((ArrayReference) left).getName();
            Record record = table.lookup(id.getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof StructReference) {
            Identifier id = (Identifier) ((StructReference) left).getName();
            Record record = table.lookup(id.getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof UnaryOperator) {
            return ((UnaryOperator) left).getTypeCategory();
        } else if (left instanceof Cast) {
            Node tail = left.getType();

            while (!(tail instanceof IdentifierType)) {
                tail = tail.getType();
            }

            //spojí všetky typy do stringu a konvertuje ich na byte
            return findType(String.join(" ", ((IdentifierType) tail).getNames()));
        } else {
            return -1;
        }
    }

    public byte getTypeCategory() {
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