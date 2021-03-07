package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

public class TernaryOperator extends Node {
    Node condition;
    Node truePart;
    Node falsePart;
    byte typeCategory;

    public TernaryOperator(Node cond, Node truePart, Node falsePart, SymbolTable table, int line) {
        this.condition = cond;
        this.truePart = truePart;
        this.falsePart = falsePart;
        setLine(line);

        if (!typeCheck(table)) {
            //TODO: Sémantická chyba
            System.out.println("Sémantická chyba na riadku " + line + "!");
        }
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "TernaryOperator:");
        if (condition != null) condition.traverse(indent + "    ");
        if (truePart != null) truePart.traverse(indent + "    ");
        if (falsePart != null) falsePart.traverse(indent + "    ");
    }

    private boolean typeCheck(SymbolTable table) {
        byte var1 = findTypeCategory(truePart, table);
        byte var2 = findTypeCategory(falsePart, table);

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

        if (var2 == Type.BOOL || var2 == Type.VOID) {
            typeCategory = -1;
            return false;
        }
        if (var1 == var2) {
            typeCategory = var1;
            return true;
        }
        if (var1 < 50 && var2 < 50 && var1 >= 29 && var2 >= 29) {
            typeCategory = -1;
            return false;
        }
        if ((var1 % 50) < 29 && (var2 % 50) < 29) {
            typeCategory = (byte) Math.max(var1, var2);
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
        } else if (left instanceof Assignment) {
            return ((Assignment) left).getLeftType(table);
        } else {
            return -1;
        }
    }

    public byte getTypeCategory() {
        return typeCategory;
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
