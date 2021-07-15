package Compiler.SymbolTable;

import Backend.InternationalizationClass;
import Backend.ProgramLogger;
import Compiler.AbstractSyntaxTree.*;
import Compiler.AbstractSyntaxTree.Enum;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.TypeChecker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Trieda pre pridávanie záznamov do symbolickej tabuľky
 *
 * @author Ivan Vykopal
 */
public final class SymbolTableFiller {

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private static final ResourceBundle bundle = InternationalizationClass.getBundle();

    /**
     * Privátny konštruktor pre triedu {@code SymbolTableFiller}.
     */
    private SymbolTableFiller() {
    }

    /**
     * Metóda na vyriešenie riadku využitia pre zadaný vrchol.
     *
     * @param node vrchol, pre ktorý zadávame využitie
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     *
     * @param checkInitialization true, ak sa má kontrolovať, či už premenná je inicializovaní, inak false
     *
     * @param checkDeclaration true, ak sa má kontrolovať, či je premenná deklarovaná, inak false
     */
    public static void resolveUsage(Node node, SymbolTable table, ErrorDatabase errorDatabase, boolean checkInitialization,
                                    boolean checkDeclaration) {
        if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                if (checkInitialization && !record.getInitialized()) {
                    if (record.getType() != Type.UNION && record.getType() != Type.STRUCT && record.getType() != Type.ENUM &&
                            (record.getType() % 50) != Type.UNION && (record.getType() % 50) != Type.STRUCT &&
                            (record.getType() % 50) != Type.ENUM && !table.isGlobal(((Identifier) node).getName())) {
                        errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-01"), "E-ST-01");
                    }
                }
                record.addUsageLine(node.getLine());
                table.setValue(((Identifier) node).getName(), record);
            } else {
                if (checkDeclaration && !isFromLibrary(((Identifier) node).getName())) {
                    errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-03"), "E-ST-03");
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
                        errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-01"), "E-ST-01");
                    }
                }
                record.addUsageLine(node.getLine());
                table.setValue(((Identifier) id).getName(), record);
            } else {
                errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-03"), "E-ST-03");
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
                errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-03"), "E-ST-03");
            }
        }
    }

    /**
     * Metóda na vyriešenie riadku využitia pre zadaný vrchol a zadaný riadok.
     *
     * @param node vrchol, pre ktorý zadávame využitie
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok, pre ktorý pridávame  využitie
     */
    public static void resolveUsage(Node node, SymbolTable table, int line) {
        if (node instanceof Identifier) {
            if (table.getChilds().size() != 0) {
                table = table.getChilds(table.getChilds().size() - 1);
            }
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
            if (table.getChilds().size() != 0) {
                table = table.getChilds(table.getChilds().size() - 1);
            }
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
            if (table.getChilds().size() != 0) {
                table = table.getChilds(table.getChilds().size() - 1);
            }
            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.addUsageLine(line);
                table.setValue(((Identifier) id).getName(), record);
            }
        }
    }

    /**
     * Metóda pre pridávanie záznamov do symbolickej tabuľky.
     *
     * @param declarator1 vrchol, ktorý pridávame do symbolickej tabuľky
     *
     * @param typedef true, ak ide o typedef (definovaný typ), inak false
     *
     * @param parameter true, ak ide o parameter funkcie, inak false
     *
     * @param structVariable true, ak ide o parameter štruktúry, inak false
     *
     * @param symbolTable symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
     */
    public static void addRecordToSymbolTable(Declarator declarator1, boolean typedef, boolean parameter,
                                        boolean structVariable, SymbolTable symbolTable, ErrorDatabase errorDatabase) {

        if (typedef) {
            if (declarator1.getDeclarator() instanceof PointerDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                int line = 0;
                int pointer = 1;
                String name = "";
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                        }
                        break;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                        line = decl_tail.getLine();
                    }
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer++;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                } else {
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                    if (struct_name != null) {
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
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
                int line = declarator1.getDeclarator().getLine();
                String type = "";
                int pointer = 0;
                short typeCategory;
                String struct_name = "";

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                        }
                        break;
                    }
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer++;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                } else {
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                    if (struct_name != null) {
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
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
                int pointer = 1;
                String name = "";
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                        }
                        break;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                        line = decl_tail.getLine();
                    }
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer++;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    type = TypeChecker.addPointers(pointer, type);
                    line = decl_tail.getLine();
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                } else {
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                    if (struct_name != null) {
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                }

                Record record = new Record(typeCategory, type, line, false);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                if (structVariable) {
                    record.setKind(Kind.STRUCT_PARAMETER);
                    record.setInitialized(true);
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
                int line = declarator1.getDeclarator().getLine();
                int pointer = 0;
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                        }
                        break;
                    }
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer++;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                } else {
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                    if (struct_name != null) {
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                }

                Record record = new Record(typeCategory, type, line, false);
                if (declarator1.getInitializer() != null) {
                    record.setInitialized(true);
                    record.addInitializationLine(line);
                }

                if (structVariable) {
                    record.setKind(Kind.STRUCT_PARAMETER);
                    record.setInitialized(true);
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
                int pointer = 0;
                int size = -1;
                String struct_name = "";
                short typeCategory;

                if (constant instanceof Constant) {
                    try {
                        size = Integer.parseInt(((Constant) constant).getValue());
                    } catch (NumberFormatException e) {
                        System.out.println(bundle.getString("sizeErr"));
                    }
                }

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer++;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                        line = decl_tail.getLine();
                    }
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                        }
                        break;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                } else {
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                    if (struct_name != null) {
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
                int pointer = 0;
                String type = "";
                String struct_name = "";
                short typeCategory;

                while (!(decl_tail instanceof IdentifierType)) {
                    if (decl_tail instanceof PointerDeclaration) {
                        pointer++;
                    }
                    if (decl_tail instanceof TypeDeclaration) {
                        name = ((TypeDeclaration) decl_tail).getDeclname();
                        line = decl_tail.getLine();
                    }
                    if (decl_tail.isEnumStructUnion()) {
                        if (decl_tail instanceof Enum) {
                            struct_name = ((Enum) decl_tail).getName();
                            type = "enum ";
                        } else if (decl_tail instanceof Struct) {
                            struct_name = ((Struct) decl_tail).getName();
                            type = "struct ";
                        } else {
                            struct_name = ((Union) decl_tail).getName();
                            type = "union ";
                        }
                        break;
                    }
                    decl_tail = decl_tail.getType();
                }

                if (type.equals("")) {
                    type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                } else {
                    type = TypeChecker.addPointers(pointer, type);
                    typeCategory = TypeChecker.findType(type, decl_tail, symbolTable);
                    if (struct_name != null) {
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
     * Metóda na vyriešenie riadku inicializácie pre zadaný vrchol.
     *
     * @param node vrchol, pre ktorý pridávame riadok inicializácie
     *
     * @param table symbolická tabuľka
     *
     * @param errorDatabase databáza chýb
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
                    errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-03"), "E-ST-03");
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
                errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-03"), "E-ST-03");
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
                errorDatabase.addErrorMessage(node.getLine(), InternationalizationClass.getErrors().getString("E-ST-03"), "E-ST-03");
            }
        }
    }

    /**
     * Metóda na vyriešenie riadku inicializácie pre zadaný vrchol a zadaný riadok.
     *
     * @param node vrchol, pre ktorý pridávame riadok inicializácie
     *
     * @param table symbolická tabuľka
     *
     * @param line riadok inicializácie, ktorý pridávame do symbolickej tabuľky
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

    /**
     * Metóda pre zistenie, či zadaný identifikátor pochádza zo štandardných knižníc.
     *
     * @param identifier identifikátor
     *
     * @return true, ak pochádza zo štandardnej knižnice, inak false
     */
    private static boolean isFromLibrary(String identifier) {
        Scanner scanner;
        try {
            File file = new File("config/libraryVariables.config");
            scanner = new java.util.Scanner(file);
        } catch (FileNotFoundException e) {
            InputStream is = SymbolTableFiller.class.getResourceAsStream("/config/libraryVariables.config");
            scanner = new java.util.Scanner(is);
        }
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().equals(identifier.trim())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            ProgramLogger.createLogger(SymbolTableFiller.class.getName()).log(Level.WARNING,
                    bundle.getString("configErr3"));
        }
        return false;
    }
}
