package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.SymbolTableFiller;
import Compiler.SymbolTable.Type;

public class TernaryOperator extends Node {
    Node condition;
    Node truePart;
    Node falsePart;
    short typeCategory;

    public TernaryOperator(Node cond, Node truePart, Node falsePart, int line, SymbolTable table, ErrorDatabase errorDatabase) {
        this.condition = cond;
        this.truePart = truePart;
        this.falsePart = falsePart;
        setLine(line);

        SymbolTableFiller.resolveUsage(condition, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(truePart, table, errorDatabase, true);
        SymbolTableFiller.resolveUsage(falsePart, table, errorDatabase, true);

        if (!typeCheck(table)) {
            if (truePart instanceof FunctionCall || falsePart instanceof FunctionCall) {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("L-SmA-03"), "L-SmA-03");
            } else {
                System.out.println("Sémantická chyba na riadku " + line + "!");
                errorDatabase.addErrorMessage(line, Error.getError("E-SmA-01"), "E-SmA-01");
            }
        }
    }

    public void resolveUsage(SymbolTable table, int line) {
        SymbolTableFiller.resolveUsage(condition, table, line);
        condition.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(truePart, table, line);
        truePart.resolveUsage(table, line);
        SymbolTableFiller.resolveUsage(falsePart, table, line);
        falsePart.resolveUsage(table, line);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "TernaryOperator:");
        if (condition != null) condition.traverse(indent + "    ");
        if (truePart != null) truePart.traverse(indent + "    ");
        if (falsePart != null) falsePart.traverse(indent + "    ");
    }

    private boolean typeCheck(SymbolTable table) {
        short var1 = findTypeCategory(truePart, table);
        short var2 = findTypeCategory(falsePart, table);

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

        if (var2 == Type.VOID) {
            typeCategory = -1;
            return false;
        }
        if (var1 == var2) {
            typeCategory = var1;
            return true;
        }
        if (var1 < 50 && var2 < 50 && var1 >= Type.UNION && var2 >= Type.UNION) {
            typeCategory = -1;
            return false;
        }
        if (var1 > 50 && var2 > 50 && (var1 % 50) < Type.UNION && (var2 % 50) < Type.UNION) {
            typeCategory = (byte) Math.max(var1, var2);
            return true;
        }
        return false;
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
        } else if (left instanceof Assignment) {
            return ((Assignment) left).getLeftType(table);
        } else {
            return -1;
        }
    }

    public short getTypeCategory() {
        return typeCategory;
    }

}
