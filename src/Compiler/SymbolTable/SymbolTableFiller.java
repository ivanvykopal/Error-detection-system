package Compiler.SymbolTable;

import Compiler.AbstractSyntaxTree.*;
import Compiler.AbstractSyntaxTree.Enum;
import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;

public final class SymbolTableFiller {

    private SymbolTableFiller() {
    }

    /**
     *
     * @param node
     * @param table
     */
    public static void resolveUsage(Node node, SymbolTable table, ErrorDatabase errorDatabase) {
        if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                if (!record.getInitialized()) {
                    errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-01"), "E-ST-01");
                }
                record.addUsageLine(node.getLine());
                table.setValue(((Identifier) node).getName(), record);
            }
        } else if (node instanceof StructReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                if (!record.getInitialized()) {
                    errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-01"), "E-ST-01");
                }
                record.addUsageLine(node.getLine());
                table.setValue(((Identifier) id).getName(), record);
            }
        } else if (node instanceof ArrayReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.addUsageLine(node.getLine());
                table.setValue(((Identifier) id).getName(), record);
            }
        }
    }

    /**
     *
     * @param declarator1
     * @param typedef
     * @param parameter
     * @param structVariable
     */
    public static void addRecordToSymbolTable(Declarator declarator1, boolean typedef, boolean parameter,
                                        boolean structVariable, SymbolTable symbolTable, ErrorDatabase errorDatabase) {

        if (typedef) {
            if (declarator1.getDeclarator() instanceof PointerDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                int line = 0;
                String name = "";
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum * ";
                            line = decl_tail.getLine();
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct * ";
                            line = decl_tail.getLine();
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union * ";
                            line = decl_tail.getLine();
                        }
                        break;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " * ";
                    line = decl_tail.getLine();
                    typeCategory = TypeChecker.findType(type);
                } else {
                    typeCategory = TypeChecker.findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                Record record = new Record(typeCategory, type, line, Kind.TYPEDEF_NAME);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                symbolTable.insert(name, record, line, Kind.TYPEDEF_NAME, errorDatabase);
                //symbolTable.insert(name, typeCategory, type, line, Kind.TYPEDEF_NAME, errorDatabase);
                System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.TYPEDEF_NAME");

            } else if (declarator1.getDeclarator() instanceof TypeDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                String name = ((TypeDeclaration) declarator1.getDeclarator()).getDeclname();
                int line = 0;
                String type = "";
                short typeCategory;
                String struct_name = "";

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                            line = decl_tail.getLine();
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                            line = decl_tail.getLine();
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                            line = decl_tail.getLine();
                        }
                        break;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    line = decl_tail.getLine();
                    typeCategory = TypeChecker.findType(type);
                } else {
                    typeCategory = TypeChecker.findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                Record record = new Record(typeCategory, type, line, Kind.TYPEDEF_NAME);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                symbolTable.insert(name, record, line, Kind.TYPEDEF_NAME, errorDatabase);

                //symbolTable.insert(name, typeCategory, type, line, Kind.TYPEDEF_NAME, errorDatabase);
                System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.TYPEDEF_NAME");
            }
        } else {
            if (declarator1.getDeclarator() instanceof PointerDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                int line = 0;
                String name = "";
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum * ";
                            line = decl_tail.getLine();
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct * ";
                            line = decl_tail.getLine();
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union * ";
                            line = decl_tail.getLine();
                        }
                        break;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " * ";
                    line = decl_tail.getLine();
                    typeCategory = TypeChecker.findType(type);
                } else {
                    typeCategory = TypeChecker.findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                Record record = new Record(typeCategory, type, line, false);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                if (structVariable) {
                    record.setKind(Kind.STRUCT_PARAMETER);
                    symbolTable.insert(name, record, line, Kind.STRUCT_PARAMETER, errorDatabase);

                    //symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_PARAMETER, errorDatabase);
                    System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_PARAMETER");
                } else {
                    if (parameter) {
                        record.setKind(Kind.PARAMETER);
                        record.setInitialized(true);
                        symbolTable.insert(name, record, line, Kind.PARAMETER, errorDatabase);
                        //symbolTable.insert(name, typeCategory, type, line, Kind.PARAMETER, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.PARAMETER");
                    } else {
                        record.setKind(Kind.VARIABLE);
                        symbolTable.insert(name, record, line, Kind.VARIABLE, errorDatabase);
                        //symbolTable.insert(name, typeCategory, type, line, Kind.VARIABLE, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.VARIABLE");
                    }
                }

            } else if (declarator1.getDeclarator() instanceof TypeDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                String name = ((TypeDeclaration) declarator1.getDeclarator()).getDeclname();
                int line = 0;
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                            line = decl_tail.getLine();
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                            line = decl_tail.getLine();
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                            line = decl_tail.getLine();
                        }
                        break;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    line = decl_tail.getLine();
                    typeCategory = TypeChecker.findType(type);
                } else {
                    typeCategory = TypeChecker.findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                Record record = new Record(typeCategory, type, line, false);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                if (structVariable) {
                    record.setKind(Kind.STRUCT_PARAMETER);
                    symbolTable.insert(name, record, line, Kind.STRUCT_PARAMETER, errorDatabase);
                    //symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_PARAMETER, errorDatabase);
                    System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_PARAMETER");
                } else {
                    if (parameter) {
                        record.setKind(Kind.PARAMETER);
                        record.setInitialized(true);
                        symbolTable.insert(name, record, line, Kind.PARAMETER, errorDatabase);
                        //symbolTable.insert(name, typeCategory, type, line, Kind.PARAMETER, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.PARAMETER");
                    } else {
                        record.setKind(Kind.VARIABLE);
                        symbolTable.insert(name, record, line, Kind.VARIABLE, errorDatabase);
                        //symbolTable.insert(name, typeCategory, type, line, Kind.VARIABLE, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.VARIABLE");
                    }
                }

            } else if (declarator1.getDeclarator() instanceof ArrayDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                String name = "";
                int line = 0;
                Node constant = ((ArrayDeclaration) declarator1.getDeclarator()).getDimension();
                String type = "";
                boolean pointer = false;
                int size = -1;
                String struct_name = "";
                short typeCategory;

                if (constant instanceof Constant) {
                    try {
                        size = Integer.parseInt(((Constant) constant).getValue());
                    } catch (NumberFormatException e) {
                        System.out.println("Chyba veľkosti poľa!");
                    }
                }

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer = true;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                    }
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                            line = decl_tail.getLine();
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                            line = decl_tail.getLine();
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                            line = decl_tail.getLine();
                        }
                        break;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (pointer) {
                    if (type.equals("")) {
                        type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " * ";
                        line = decl_tail.getLine();
                        typeCategory = TypeChecker.findType(type);
                    } else {
                        type += "* ";
                        typeCategory = TypeChecker.findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                } else {
                    if (type.equals("")) {
                        type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                        line = decl_tail.getLine();
                        typeCategory = TypeChecker.findType(type);
                    } else {
                        typeCategory = TypeChecker.findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                }

                Record record = new Record(typeCategory, type, line, false);
                record.setInitialized(true);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                if (size < 0) {
                    if (structVariable) {
                        record.setKind(Kind.STRUCT_ARRAY_PARAMETER);
                        symbolTable.insert(name, record, line, Kind.STRUCT_ARRAY_PARAMETER, errorDatabase);
                        //symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_ARRAY_PARAMETER, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_ARRAY_PARAMETER");
                    } else {
                        if (parameter) {
                            record.setKind(Kind.ARRAY_PARAMETER);
                            symbolTable.insert(name, record, line, Kind.ARRAY_PARAMETER, errorDatabase);
                            //symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY_PARAMETER, errorDatabase);
                            System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.ARRAY_PARAMETER");
                        } else {
                            record.setKind(Kind.ARRAY);
                            symbolTable.insert(name, record, line, Kind.ARRAY, errorDatabase);
                            //symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY, errorDatabase);
                            System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.ARRAY");
                        }
                    }
                } else {
                    if (structVariable) {
                        record.setKind(Kind.STRUCT_ARRAY_PARAMETER);
                        record.setSize(size);
                        symbolTable.insert(name, record, line, Kind.STRUCT_ARRAY_PARAMETER, errorDatabase);
                        //symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_ARRAY_PARAMETER, size, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_ARRAY_PARAMETER, " + size);
                    } else {
                        if (parameter) {
                            record.setKind(Kind.ARRAY_PARAMETER);
                            record.setSize(size);
                            symbolTable.insert(name, record, line, Kind.ARRAY_PARAMETER, errorDatabase);
                            //symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY_PARAMETER, size, errorDatabase);
                            System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.ARRAY_PARAMETER, " + size);
                        } else {
                            record.setSize(Kind.ARRAY);
                            record.setSize(size);
                            symbolTable.insert(name, record, line, Kind.ARRAY, errorDatabase);
                            //symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY, size, errorDatabase);
                            System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.ARRAY, " + size);
                        }
                    }
                }

            } else if (declarator1.getDeclarator() instanceof FunctionDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                String name = "";
                int line = 0;
                ParameterList parameters = (ParameterList) ((FunctionDeclaration) declarator1.getDeclarator()).getArguments();
                boolean pointer = false;
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer = true;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                    }
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                            line = decl_tail.getLine();
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                            line = decl_tail.getLine();
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                            line = decl_tail.getLine();
                        }
                        break;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (pointer) {
                    if (type.equals("")) {
                        type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " * ";
                        line = decl_tail.getLine();
                        typeCategory = TypeChecker.findType(type);
                    } else {
                        type += "* ";
                        typeCategory = TypeChecker.findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                } else {
                    if (type.equals("")) {
                        type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                        line = decl_tail.getLine();
                        typeCategory = TypeChecker.findType(type);
                    } else {
                        typeCategory = TypeChecker.findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                }

                Record record = new Record(typeCategory, type, line, false, Kind.FUNCTION);
                record.setInitialized(true);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                //symbolTable.insert(name, record, line, Kind.FUNCTION, errorDatabase);
                //symbolTable.insert(name, typeCategory, type, line, Kind.FUNCTION, errorDatabase);
                //System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.FUNCTION");

                //Record record1 = symbolTable.lookup(name);

                if (parameters != null) {
                    for (Node param : parameters.getParameters()) {
                        if (param instanceof EllipsisParam) {
                            record.getParameters().add("...");
                            symbolTable.setValue(name, record);
                            System.out.println("Update: " + name);
                            break;
                        }
                        String param_name = ((DeclarationNode) param).getName();

                        //pridanie parametra pre funkciu
                        record.getParameters().add(param_name);
                        //symbolTable.setValue(name, record);
                        System.out.println("Update: " + name);
                    }
                }

                symbolTable.insert(name, record, line, Kind.FUNCTION, errorDatabase);
                //symbolTable.insert(name, typeCategory, type, line, Kind.FUNCTION, errorDatabase);
                System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.FUNCTION");
            }
        }
    }

    /**
     *
     * @param table
     */
    public static void resolveInitialization(Node node, SymbolTable table) {
        if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                record.setInitialized(true);
                record.addInitializationLine(node.getLine());
                table.setValue(((Identifier) node).getName(), record);
            }
        } else if (node instanceof StructReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.setInitialized(true);
                record.addInitializationLine(node.getLine());
                table.setValue(((Identifier) id).getName(), record);
            }
        } else if (node instanceof ArrayReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.addInitializationLine(node.getLine());
                table.setValue(((Identifier) id).getName(), record);
            }
        }
    }
}
