package Compiler.AbstractSyntaxTree;

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
    protected byte findType(String type) {
        //vymazanie poslednej medzery
        byte pointer = 0;
        //riešenie smerníkov
        if (type.contains("*")) {
            pointer = 50;
            type = type.replace("* ", "");
        }

        if (type.equals("")) {
            return -1;
        }

        //riešenie typov
        switch (type) {
            case "char ": return (byte) (Type.CHAR + pointer);                                              // char
            case "signed char ": return (byte) (Type.SIGNEDCHAR + pointer);                                 // signed char
            case "unsigned char ": return (byte) (Type.UNSIGNEDCHAR + pointer);                             // unsigned char
            case "short ": return (byte) (Type.SHORT + pointer);                                            // short
            case "signed short ": return (byte) (Type.SIGNEDSHORT + pointer);                               // signed short
            case "unsigned short ": return (byte) (Type.UNSIGNEDSHORT + pointer);                           // unsigned short
            case "int ": return (byte) (Type.INT + pointer);                                                // int
            case "signed ": return (byte) (Type.SIGNED + pointer);                                          // signed
            case "signed int ": return (byte) (Type.SIGNEDINT + pointer);                                   // signed int
            case "unsigned ": return (byte) (Type.UNSIGNED + pointer);                                      // unsigned
            case "unsigned int ": return (byte) (Type.UNSIGNEDINT + pointer);                               // unsigned int
            case "short int ": return (byte) (Type.SHORTINT + pointer);                                     // short int
            case "signed short int ": return (byte) (Type.SIGNEDSHORTINT + pointer);                        // signed short int
            case "unsigned short int ": return (byte) (Type.UNSIGNEDSHORTINT + pointer);                    // unsigned short int
            case "long ": return (byte) (Type.LONG + pointer);                                              // long
            case "signed long ": return (byte) (Type.SIGNEDLONG + pointer);                                 // signed long
            case "unsigned long ": return (byte) (Type.UNSIGNEDLONG + pointer);                             // unsigned long
            case "long int ": return (byte) (Type.LONGINT + pointer);                                       // long int
            case "signed long int ": return (byte) (Type.SIGNEDLONGINT + pointer);                          // signed long int
            case "unsigned long int ": return (byte) (Type.UNSIGNEDLONGINT + pointer);                      // unsigned long int
            case "long long ": return (byte) (Type.LONGLONG + pointer);                                     // long long
            case "long long int ": return (byte) (Type.LONGLONGINT + pointer);                              // long long int
            case "signed long long ": return (byte) (Type.SIGNEDLONGLONG + pointer);                        // signed long long
            case "signed long long int ": return (byte) (Type.SIGNEDLONGLONGINT + pointer);                 // signed long long int
            case "unsigned long long ": return (byte) (Type.UNSIGNEDLONGLONG + pointer);                    // unsigned long long
            case "unsigned long long int ": return (byte) (Type.UNSIGNEDLONGLONGINT + pointer);             // unsigned long long int
            case "float ": return (byte) (Type.FLOAT + pointer);                                            // float
            case "double ": return (byte) (Type.DOUBLE + pointer);                                          // double
            case "long double ": return (byte) (Type.LONGDOUBLE + pointer);                                 // long double
            case "union ": return (byte) (Type.UNION + pointer);                                            // union
            case "struct ": return (byte) (Type.STRUCT + pointer);                                          // struct
            case "enum ": return (byte) (Type.ENUM + pointer);                                              // enum
            case "void ": return (byte) (Type.VOID + pointer);                                              // void
            case "string ": return Type.STRING;
            default: return Type.TYPEDEF_TYPE;                                                              // vlastný typ
        }
    }


}