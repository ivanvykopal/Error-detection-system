package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.*;

public class Assignment extends Node {
    Node left;
    Node right;
    String operator;

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

    private short findTypeCategory(Node left, SymbolTable table) {
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
            return TypeChecker.findType(((Constant) left).getTypeSpecifier() + " ");
        } else if (left instanceof FunctionCall) {
            Node id = left.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -2;                                                      //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
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
            return TypeChecker.findType(type);
        } else if (left instanceof TernaryOperator) {
            return ((TernaryOperator) left).getTypeCategory();
        } else {
            return -1;
        }
    }

    public short getLeftType(SymbolTable table) {
        return findTypeCategory(left, table);
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveInitialization(left, table, line);
        SymbolTableFiller.resolveUsage(right, table, line);
        right.resolveUsage(table, line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Assignmnent: ");
        if (left != null) left.traverse(indent + "    ");
        if (operator != null) System.out.println(indent + operator);
        if (right != null) right.traverse(indent + "    ");
    }

}
