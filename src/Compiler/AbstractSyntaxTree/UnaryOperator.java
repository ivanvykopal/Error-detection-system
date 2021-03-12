package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

public class UnaryOperator extends Node {
    Node expression;
    String operator;
    short typeCategory;

    public UnaryOperator(Node expr, String op, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.expression = expr;
        this.operator = op;
        setLine(line);

        if (operator.equals("&")) {
            SymbolTableFiller.resolveUsage(expression, table, errorDatabase, false);
        } else {
            SymbolTableFiller.resolveUsage(expression, table, errorDatabase, true);
        }

        if (!typeCheck(table)) {
            if (expression instanceof FunctionCall) {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("L-SmA-03"), "L-SmA-03");
            } else {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("E-SmA-01"), "E-SmA-01");
            }
        }
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(expression, table, line);
        expression.resolveUsage(table, line);
    }

    private boolean typeCheck(SymbolTable table) {
        short type = findTypeCategory(expression, table);
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
        if (type == -1) {
            typeCategory = -1;
            return false;
        }
        if (type == -2) {
            typeCategory = -2;                                                              //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            return true;
        }
        if (type < Type.UNION) {
            typeCategory = type;
            return true;
        } else if (type > 50 && (type % 50) < Type.UNION) {
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
            return TypeChecker.findType(((Constant) left).getTypeSpecifier());
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
            return TypeChecker.findType(type);
        } else {
            return -1;
        }
    }

    public Node getExpression() {
        return expression;
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

}