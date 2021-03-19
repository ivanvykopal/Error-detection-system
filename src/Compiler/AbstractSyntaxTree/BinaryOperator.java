package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

public class BinaryOperator extends Node {
    Node left;
    Node right;
    String operator;
    short typeCategory;

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

    private short findTypeCategory(Node left, SymbolTable table) {
        if (left instanceof BinaryOperator) {
            return ((BinaryOperator) left).getTypeCategory();
        } else if (left instanceof Identifier) {
            //nájsť v symbolickej tabuľke
            Record record = table.lookup(((Identifier) left).getName());
            if (record == null) {
                return -2;
            } else {
                return record.getType();
            }
        } else if (left instanceof Constant) {
            return TypeChecker.findType(((Constant) left).getTypeSpecifier() + " ", null, table);
        } else if (left instanceof FunctionCall) {
            Node id = left.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -2;
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
            return TypeChecker.findType(type, tail, table);
        } else if (left instanceof TernaryOperator) {
            return ((TernaryOperator) left).getTypeCategory();
        } else  {
            return -1;
        }
    }

    public short getTypeCategory() {
        return typeCategory;
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(left, table, line);
        SymbolTableFiller.resolveUsage(right, table, line);
        left.resolveUsage(table, line);
        right.resolveUsage(table, line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "BinaryOperator: ");
        if (left != null) left.traverse(indent + "    ");
        if (operator != null) System.out.println(indent + operator);
        if (right != null) right.traverse(indent + "    ");
    }


}
