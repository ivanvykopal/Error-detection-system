package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

public class Assignment extends Node {
    Node left;
    Node right;
    String operator;

    public Assignment(Node left, String op, Node right, SymbolTable table, int line) {
        this.left = left;
        this.operator = op;
        this.right = right;
        setLine(line);

        if (!typeCheck(table)) {
            //TODO: Sémantická chyba
            System.out.println("Sémantická chyba na riadku " + line + "!");
        }
    }

    private boolean typeCheck(SymbolTable table) {
        byte var1 = findTypeCategory(left, table);
        byte var2 = findTypeCategory(right, table);
        if (var2 == -2) {
            return true;
        }

        if ((left instanceof ArrayDeclaration) && (var1 == Type.CHAR || var1 == Type.SIGNEDCHAR || var1 == Type.UNSIGNEDCHAR)
                && var2 == Type.STRING) {
            return true;
        }

        if (var2 == Type.BOOL || var2 == Type.VOID) {
            return false;
        }
        if (var1 == var2) {
            return true;
        }
        if (var1 < 50 && var2 < 50 && var1 >= 29 && var2 >= 29) {
            return false;
        }
        if ((var1 % 50) < 29 && (var2 % 50) < 29) {
            return true;
        }
        return false;
    }

    private byte findTypeCategory(Node left, SymbolTable table) {
        if (left instanceof BinaryOperator) {
            return ((BinaryOperator) left).getTypeCategory();
        } else if (left instanceof Identifier) {
            //nájsť v symbolickej tabuľke
            Record record = table.lookup(((Identifier) left).getName());
            if (record == null) {
                return -2;                                                      //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (left instanceof Constant) {
            return findType(((Constant) left).getTypeSpecifier() + " ");
        } else if (left instanceof FunctionCall) {
            Identifier id = (Identifier) ((FunctionCall) left).getName();
            Record record = table.lookup(id.getName());
            if (record == null) {
                return -2;                                                      //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
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
        } else if (left instanceof TernaryOperator) {
            return ((TernaryOperator) left).getTypeCategory();
        } else {
            return -1;
        }
    }

    public byte getLeftType(SymbolTable table) {
        return findTypeCategory(left, table);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Assignmnent: ");
        if (left != null) left.traverse(indent + "    ");
        if (operator != null) System.out.println(indent + operator);
        if (right != null) right.traverse(indent + "    ");
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
