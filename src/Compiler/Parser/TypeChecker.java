package Compiler.Parser;

import Compiler.AbstractSyntaxTree.*;
import Compiler.AbstractSyntaxTree.Enum;
import Compiler.SymbolTable.Kind;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

/**
 * Trieda obsahujúca metódy potrebné pre zisťovanie typov jednotlivých premenných, respektíve identifikátorov.
 *
 * @author Ivan Vykopal
 */
public final class TypeChecker {

    /**
     * Privátny konštruktor.
     */
    private TypeChecker() {
    }

    /**
     * Metóda na zistenie typu.
     *
     * @param type typ (String)
     *
     * @return typ (byte)
     */
    public static short findType(String type, Node node, SymbolTable symbolTable) {
        //vymazanie poslednej medzery
        short pointer = 0;
        for (int i = 0; i < type.length(); i++){
            if (type.charAt(i) == '*') {
                pointer += 50;
            }
        }
        type = type.replace("* ", "");

        if (type.equals("")) {
            return -1;
        }

        //riešenie typov
        switch (type) {
            case "char ": return (short) (Type.CHAR + pointer);                                              // char
            case "signed char ": return (short) (Type.SIGNEDCHAR + pointer);                                 // signed char
            case "unsigned char ": return (short) (Type.UNSIGNEDCHAR + pointer);                             // unsigned char
            case "short ": return (short) (Type.SHORT + pointer);                                            // short
            case "signed short ": return (short) (Type.SIGNEDSHORT + pointer);                               // signed short
            case "unsigned short ": return (short) (Type.UNSIGNEDSHORT + pointer);                           // unsigned short
            case "int ": return (short) (Type.INT + pointer);                                                // int
            case "signed ": return (short) (Type.SIGNED + pointer);                                          // signed
            case "signed int ": return (short) (Type.SIGNEDINT + pointer);                                   // signed int
            case "unsigned ": return (short) (Type.UNSIGNED + pointer);                                      // unsigned
            case "unsigned int ": return (short) (Type.UNSIGNEDINT + pointer);                               // unsigned int
            case "short int ": return (short) (Type.SHORTINT + pointer);                                     // short int
            case "signed short int ": return (short) (Type.SIGNEDSHORTINT + pointer);                        // signed short int
            case "unsigned short int ": return (short) (Type.UNSIGNEDSHORTINT + pointer);                    // unsigned short int
            case "long ": return (short) (Type.LONG + pointer);                                              // long
            case "signed long ": return (short) (Type.SIGNEDLONG + pointer);                                 // signed long
            case "unsigned long ": return (short) (Type.UNSIGNEDLONG + pointer);                             // unsigned long
            case "long int ": return (short) (Type.LONGINT + pointer);                                       // long int
            case "signed long int ": return (short) (Type.SIGNEDLONGINT + pointer);                          // signed long int
            case "unsigned long int ": return (short) (Type.UNSIGNEDLONGINT + pointer);                      // unsigned long int
            case "long long ": return (short) (Type.LONGLONG + pointer);                                     // long long
            case "long long int ": return (short) (Type.LONGLONGINT + pointer);                              // long long int
            case "signed long long ": return (short) (Type.SIGNEDLONGLONG + pointer);                        // signed long long
            case "signed long long int ": return (short) (Type.SIGNEDLONGLONGINT + pointer);                 // signed long long int
            case "unsigned long long ": return (short) (Type.UNSIGNEDLONGLONG + pointer);                    // unsigned long long
            case "unsigned long long int ": return (short) (Type.UNSIGNEDLONGLONGINT + pointer);             // unsigned long long int
            case "float ": return (short) (Type.FLOAT + pointer);                                            // float
            case "double ": return (short) (Type.DOUBLE + pointer);                                          // double
            case "long double ": return (short) (Type.LONGDOUBLE + pointer);                                 // long double
            case "union ": return (short) (Type.UNION + pointer);                                            // union
            case "struct ": return (short) (Type.STRUCT + pointer);                                          // struct
            case "enum ": return (short) (Type.ENUM + pointer);                                              // enum
            case "void ": return (short) (Type.VOID + pointer);                                              // void
            case "string ": return Type.STRING;
            default:                                                                                        // vlastný typ
                if (node == null) {
                    return Type.TYPEDEF_TYPE;
                }
                Node id;
                if (node instanceof IdentifierType) {
                    id = node;
                } else {
                    id = node.getType();

                    while (!(id instanceof IdentifierType)) {
                        id = id.getType();
                    }
                }

                String name = String.join(" ", ((IdentifierType) id).getNames());
                Record record = symbolTable.lookup(name);
                if (record != null && record.getKind() == Kind.TYPEDEF_NAME) {
                    return record.getType();
                } else {
                    return Type.TYPEDEF_TYPE;
                }
        }
    }

    /**
     * Metóda na zistienie typu inicializačnej hodnoty.
     *
     * @param initializer vrchol pre inicializačnú hodnotu
     *
     * @param symbolTable symbolická tabuľka
     *
     * @return typ inicializačnej hodnoty (numerická hodnota)
     */
    public static short getInitializer(Node initializer, SymbolTable symbolTable) {
        if (initializer instanceof InitializationList) {
            return -1;
        } else if (initializer instanceof BinaryOperator) {
            return ((BinaryOperator) initializer).getTypeCategory();
        } else if (initializer instanceof Assignment) {
            return ((Assignment) initializer).getLeftType(symbolTable);
        } else if (initializer instanceof TernaryOperator) {
            return ((TernaryOperator) initializer).getTypeCategory();
        } else if (initializer instanceof Cast) {
            Node tail = initializer.getType();
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
            return findType(type, tail, symbolTable);
        } else if (initializer instanceof UnaryOperator) {
            return ((UnaryOperator) initializer).getTypeCategory();
        } else if (initializer instanceof Identifier) {
            //nájsť v symbolickej tabuľke
            Record record = symbolTable.lookup(((Identifier) initializer).getName());
            if (record == null) {
                return -2;                                                                  //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (initializer instanceof Constant) {
            return findType(((Constant) initializer).getTypeSpecifier() + " ", null, symbolTable);
        } else if (initializer instanceof FunctionCall) {
            Node id = initializer.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = symbolTable.lookup(((Identifier) id).getName());
            if (record == null) {
                return -2;                                                                  //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (initializer instanceof ArrayReference) {
            Node id = initializer.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = symbolTable.lookup(((Identifier) id).getName());
            if (record == null) {
                return -128;
            } else {
                return record.getType();
            }
        } else if (initializer instanceof  StructReference) {
            Node id = initializer.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = symbolTable.lookup(((Identifier) id).getName());
            if (record == null) {
                return -128;
            } else {
                return record.getType();
            }
        } else {
            return -128;
        }
    }

    /**
     * Metóda pre nájdenie kategórie typu pre zadaný vrchol.
     *
     * @param node vrchol, ktorého typ zisťujeme
     *
     * @param table symbolická tabuľka
     *
     * @return typ daného vrcholu
     */
    public static short findTypeCategory(Node node, SymbolTable table) {
        if (node instanceof BinaryOperator) {
            return ((BinaryOperator) node).getTypeCategory();
        } else if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record == null) {
                return -2;                                                      //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (node instanceof Constant) {
            return TypeChecker.findType(((Constant) node).getTypeSpecifier() + " ", null, table);
        } else if (node instanceof FunctionCall) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -2;                                                      //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (node instanceof ArrayReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (node instanceof StructReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (node instanceof UnaryOperator) {
            return ((UnaryOperator) node).getTypeCategory();
        } else if (node instanceof Cast) {
            Node tail = node.getType();
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

            return TypeChecker.findType(type, tail, table);
        } else if (node instanceof TernaryOperator) {
            return ((TernaryOperator) node).getTypeCategory();
        } else if (node instanceof Assignment) {
            return ((Assignment) node).getLeftType(table);
        } else {
            return -1;
        }
    }
}
