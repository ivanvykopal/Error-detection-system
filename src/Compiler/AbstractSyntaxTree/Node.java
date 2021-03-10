package Compiler.AbstractSyntaxTree;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

public abstract class Node {
    protected int line;

    public Node() {

    }

    abstract public void traverse(String indent);

    abstract public boolean isNone();

    abstract public boolean isEmpty();

    abstract public boolean isEnumStructUnion();

    abstract public boolean isTypeDeclaration();

    abstract public Node getType();

    public Node getNameNode() {
        return null;
    };

    abstract public void addType(Node type);

    abstract public boolean isIdentifierType();

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    /**
     * Funkcia na zistenie typu.
     * @param type typ (String)
     * @return typ (byte)
     */
    protected short findType(String type) {
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
            default: return Type.TYPEDEF_TYPE;                                                              // vlastný typ
        }
    }

    /**
     *
     * @param node
     * @param table
     */
    protected void resolveUsage(Node node, SymbolTable table, ErrorDatabase errorDatabase) {
        if (node instanceof Identifier) {
            Record record = table.lookup(((Identifier) node).getName());
            if (record != null) {
                if (!record.getInitialized()) {
                    errorDatabase.addErrorMessage(line, Error.getError("E-ST-01"), "E-ST-01");
                }
                record.addUsageLine(line);
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
                    errorDatabase.addErrorMessage(line, Error.getError("E-ST-01"), "E-ST-01");
                }
                record.addUsageLine(line);
                table.setValue(((Identifier) id).getName(), record);
            }
        } else if (node instanceof ArrayReference) {
            Node id = node.getNameNode();

            while (!(id instanceof Identifier)) {
                id = id.getNameNode();
            }

            Record record = table.lookup(((Identifier) id).getName());
            if (record != null) {
                record.addUsageLine(line);
                table.setValue(((Identifier) id).getName(), record);
            }
        }
    }


}