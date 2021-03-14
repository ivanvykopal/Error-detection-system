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
     * @param errorDatabase
     */
    public static void resolveUsage(Node node, SymbolTable table, ErrorDatabase errorDatabase, boolean checkInitializatiaon) {
        if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                if (checkInitializatiaon && !record.getInitialized()) {
                    if (record.getType() != Type.UNION && record.getType() != Type.STRUCT && record.getType() != Type.ENUM &&
                            (record.getType() % 50) != Type.UNION && (record.getType() % 50) != Type.STRUCT &&
                            (record.getType() % 50) != Type.ENUM) {
                        errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-01"), "E-ST-01");
                    }
                }
                record.addUsageLine(node.getLine());
                table.setValue(((Identifier) node).getName(), record);
            } else {
                if (!isFromLibrary(((Identifier) node).getName())) {
                    errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-03"), "E-ST-03");
                }
            }
        } else if (node instanceof StructReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                if (!record.getInitialized()) {
                    if (record.getType() != Type.UNION && record.getType() != Type.STRUCT && record.getType() != Type.ENUM &&
                            (record.getType() % 50) != Type.UNION && (record.getType() % 50) != Type.STRUCT &&
                            (record.getType() % 50) != Type.ENUM) {
                        errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-01"), "E-ST-01");
                    }
                }
                record.addUsageLine(node.getLine());
                table.setValue(((Identifier) id).getName(), record);
            } else {
                errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-03"), "E-ST-03");
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
            } else {
                errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-03"), "E-ST-03");
            }
        }
    }

    /**
     *
     * @param node
     * @param table
     * @param line
     */
    public static void resolveUsage(Node node, SymbolTable table, int line) {
        if (node instanceof Identifier) {
            table = table.getChilds(table.getChilds().size() - 1);
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                record.addUsageLine(line);
                table.setValue(((Identifier) node).getName(), record);
            }
        } else if (node instanceof StructReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }
            table = table.getChilds(table.getChilds().size() - 1);
            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.addUsageLine(line);
                table.setValue(((Identifier) id).getName(), record);
            }
        } else if (node instanceof ArrayReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }
            table = table.getChilds(table.getChilds().size() - 1);
            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.addUsageLine(line);
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
                } else {
                    if (parameter) {
                        record.setKind(Kind.PARAMETER);
                        record.setInitialized(true);
                        record.addInitializationLine(line);
                        symbolTable.insert(name, record, line, Kind.PARAMETER, errorDatabase);
                    } else {
                        record.setKind(Kind.VARIABLE);
                        symbolTable.insert(name, record, line, Kind.VARIABLE, errorDatabase);
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
                } else {
                    if (parameter) {
                        record.setKind(Kind.PARAMETER);
                        record.setInitialized(true);
                        record.addInitializationLine(line);
                        symbolTable.insert(name, record, line, Kind.PARAMETER, errorDatabase);
                    } else {
                        record.setKind(Kind.VARIABLE);
                        symbolTable.insert(name, record, line, Kind.VARIABLE, errorDatabase);
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
                    } else {
                        if (parameter) {
                            record.setKind(Kind.ARRAY_PARAMETER);
                            record.addInitializationLine(line);
                            symbolTable.insert(name, record, line, Kind.ARRAY_PARAMETER, errorDatabase);
                        } else {
                            record.setKind(Kind.ARRAY);
                            symbolTable.insert(name, record, line, Kind.ARRAY, errorDatabase);
                        }
                    }
                } else {
                    if (structVariable) {
                        record.setKind(Kind.STRUCT_ARRAY_PARAMETER);
                        record.setSize(size);
                        symbolTable.insert(name, record, line, Kind.STRUCT_ARRAY_PARAMETER, errorDatabase);
                    } else {
                        if (parameter) {
                            record.setKind(Kind.ARRAY_PARAMETER);
                            record.setSize(size);
                            record.addInitializationLine(line);
                            symbolTable.insert(name, record, line, Kind.ARRAY_PARAMETER, errorDatabase);
                        } else {
                            record.setKind(Kind.ARRAY);
                            record.setSize(size);
                            symbolTable.insert(name, record, line, Kind.ARRAY, errorDatabase);
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

                if (parameters != null) {
                    for (Node param : parameters.getParameters()) {
                        if (param instanceof EllipsisParam) {
                            record.getParameters().add("...");
                            symbolTable.setValue(name, record);
                            break;
                        }
                        String param_name = ((DeclarationNode) param).getName();

                        //pridanie parametra pre funkciu
                        record.getParameters().add(param_name);
                    }
                }

                symbolTable.insert(name, record, line, Kind.FUNCTION, errorDatabase);
            }
        }
    }

    /**
     *
     * @param node
     * @param table
     * @param errorDatabase
     */
    public static void resolveInitialization(Node node, SymbolTable table, ErrorDatabase errorDatabase) {
        if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                record.setInitialized(true);
                record.addInitializationLine(node.getLine());
                table.setValue(((Identifier) node).getName(), record);
            } else {
                if (!isFromLibrary(((Identifier) node).getName())) {
                    errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-03"), "E-ST-03");
                }
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
            } else {
                errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-03"), "E-ST-03");
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
            } else {
                errorDatabase.addErrorMessage(node.getLine(), Error.getError("E-ST-03"), "E-ST-03");
            }
        }
    }

    /**
     *
     * @param node
     * @param table
     * @param line
     */
    public static void resolveInitialization(Node node, SymbolTable table, int line) {
        if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                record.setInitialized(true);
                record.addInitializationLine(line);
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
                record.addInitializationLine(line);
                table.setValue(((Identifier) id).getName(), record);
            }
        } else if (node instanceof ArrayReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.addInitializationLine(line);
                table.setValue(((Identifier) id).getName(), record);
            }
        }
    }


    private static boolean isFromLibrary(String identifier) {
        switch (identifier) {
            case "FLT_ROUNDS":
            case "FLT_RADIX":
            case "FLT_MANT_DIG":
            case "DBL_MANT_DIG":
            case "LDBL_MANT_DIG":
            case "FLT_DIG":
            case "DBL_DIG":
            case "LDBL_DIG":
            case "FLT_MIN_EXP":
            case "DBL_MIN_EXP":
            case "LDBL_MIN_EXP":
            case "FLT_MIN_10_EXP":
            case "DBL_MIN_10_EXP":
            case "LDBL_MIN_10_EXP":
            case "FLT_MAX_EXP":
            case "DBL_MAX_EXP":
            case "LDBL_MAX_EXP":
            case "FLT_MAX_10_EXP":
            case "DBL_MAX_10_EXP":
            case "LDBL_MAX_10_EXP":
            case "FLT_MAX":
            case "DBL_MAX":
            case "LDBL_MAX":
            case "FLT_EPSILON":
            case "DBL_EPSILON":
            case "LDBL_EPSILON":
            case "FLT_MIN":
            case "DBL_MIN":
            case "LDBL_MIN":
            case "CHAR_BIT":
            case "SCHAR_MIN":
            case "SCHAR_MAX":
            case "UCHAR_MAX":
            case "CHAR_MIN":
            case "CHAR_MAX":
            case "MB_LEN_MAX":
            case "SHRT_MIN":
            case "SHRT_MAX":
            case "USHRT_MAX":
            case "INT_MIN":
            case "INT_MAX":
            case "UINT_MAX":
            case "LONG_MIN":
            case "LONG_MAX":
            case "ULONG_MAX":
            case "LC_ALL":
            case "LC_COLLATE":
            case "LC_CTYPE":
            case "LC_MONETARY":
            case "LC_NUMERIC":
            case "LC_TIME":
            case "HUGE_VAL":
            case "jmp_buf":
            case "sig_atomic_t":
            case "SIG_DFL":
            case "SIG_ERR":
            case "SIG_IGN":
            case "SIGABRT":
            case "SIGFPE":
            case "SIGILL":
            case "SIGINT":
            case "SIGSEGV":
            case "SIGTERM":
            case "NULL":
            case "_IOFBF":
            case "_IOLBF":
            case "_IONBF":
            case "BUFSIZ":
            case "EOF":
            case "FOPEN_MAX":
            case "FILENAME_MAX":
            case "L_tmpnam":
            case "SEEK_CUR":
            case "SEEK_END":
            case "SEEK_SET":
            case "TMP_MAX":
            case "stderr":
            case "stdin":
            case "stdout":
            case "EXIT_FAILURE":
            case "EXIT_SUCCESS":
            case "RAND_MAX":
            case "MB_CUR_MAX":
            case "CLOCKS_PER_SEC":
            case "__FILE__":
            case "__DATE__":
            case "__TIME__":
            case "__LINE__":
            case "__STDC__":
                return true;
            default:
                return false;
        }
    }
}
