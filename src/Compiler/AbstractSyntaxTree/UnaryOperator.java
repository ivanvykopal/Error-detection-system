package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import Compiler.SymbolTable.Type;

public class UnaryOperator extends Node {
    Node expression;
    String operator;
    byte typeCategory;

    public UnaryOperator(Node expr, String op, SymbolTable table) {
        this.expression = expr;
        this.operator = op;

        /*if (!typeCheck(table)) {
            //TODO: Sémantická chyba
            System.out.println("Sémantická chyba!");
        }*/
    }

    private boolean typeCheck(SymbolTable table) {
        byte type = findTypeCategory(expression, table);
        if (type < 29) {
            typeCategory = type;
            return true;
        } else {
            typeCategory = -1;
            return false;
        }
    }

    private byte findTypeCategory(Node left, SymbolTable table) {
        if (left instanceof  Identifier) {
            //nájsť v symbolickej tabuľke
            Record record = table.lookup(((Identifier) left).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof Constant) {
            return findType(((Constant) left).getTypeSpecifier());
        } else if (left instanceof FunctionCall) {
            Identifier id = (Identifier) ((FunctionCall) left).getName();
            Record record = table.lookup(id.getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof ArrayReference) {
            Identifier id = (Identifier) ((ArrayReference) left).getName();
            Record record = table.lookup(id.getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof StructReference) {
            Identifier id = (Identifier) ((StructReference) left).getName();
            Record record = table.lookup(id.getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof UnaryOperator) {
            return ((UnaryOperator) left).getTypeCategory();
        } else if (left instanceof Cast) {
            Node tail = left.getType();

            while (!(tail instanceof IdentifierType)) {
                tail = tail.getType();
            }

            //spojí všetky typy do stringu a konvertuje ich na byte
            return findType(String.join(" ", ((IdentifierType) tail).getNames()));
        } else {
            return -1;
        }
    }

    /**
     * Funkcia na zistenie typu.
     * @param type typ (String)
     * @return typ (byte)
     */
    private byte findType(String type) {
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
        switch (type.hashCode()) {
            case 3052374: return (byte) (Type.CHAR + pointer);                      // char
            case -359586342: return (byte) (Type.SIGNEDCHAR + pointer);             // signed char
            case 986197409: return (byte) (Type.UNSIGNEDCHAR + pointer);            // unsigned char
            case 109413500: return (byte) (Type.SHORT + pointer);                   // short
            case 1752515192: return (byte) (Type.SIGNEDSHORT + pointer);            // signed short
            case 522138513: return (byte) (Type.UNSIGNEDSHORT + pointer);           // unsigned short
            case 104431: return (byte) (Type.INT + pointer);                        // int
            case -902467812: return (byte) (Type.SIGNED + pointer);                 // signed
            case -981424917: return (byte) (Type.SIGNEDINT + pointer);              // signed int
            case -15964427: return (byte) (Type.UNSIGNED + pointer);                // unsigned
            case 1140197444: return (byte) (Type.UNSIGNEDINT + pointer);            // unsigned int
            case -2029581749: return (byte) (Type.SHORTINT + pointer);              // short int
            case -827364793: return (byte) (Type.SIGNEDSHORTINT + pointer);         // signed short int
            case 1314465504: return (byte) (Type.UNSIGNEDSHORTINT + pointer);       // unsigned short int
            case 3327612: return (byte) (Type.LONG + pointer);                      // long
            case -359311104: return (byte) (Type.SIGNEDLONG + pointer);             // signed long
            case 986472647: return (byte) (Type.UNSIGNEDLONG + pointer);            // unsigned long
            case -2075964341: return (byte) (Type.LONGINT + pointer);               // long int
            case 2119236815: return (byte) (Type.SIGNEDLONGINT + pointer);          // signed long int
            case 1218496790: return (byte) (Type.UNSIGNEDLONGINT + pointer);        // unsigned long int
            case 69705120: return (byte) (Type.LONGLONG + pointer);                 // long long
            case 1173352815: return (byte) (Type.LONGLONGINT + pointer);            // long long int
            case 1271922076: return (byte) (Type.SIGNEDLONGLONG + pointer);         // signed long long
            case -1037044885: return (byte) (Type.SIGNEDLONGLONGINT + pointer);     // signed long long int
            case -881214923: return (byte) (Type.UNSIGNEDLONGLONG + pointer);       // unsigned long long
            case -1492665468: return (byte) (Type.UNSIGNEDLONGLONGINT + pointer);   // unsigned long long int
            case 97526364: return (byte) (Type.FLOAT + pointer);                    // float
            case -1325958191: return (byte) (Type.DOUBLE + pointer);                // double
            case -1961682443: return (byte) (Type.LONGDOUBLE + pointer);            // long double
            case 111433423: return (byte) (Type.UNION + pointer);                   // union
            case -891974699: return (byte) (Type.STRUCT + pointer);                 // struct
            case 3118337: return (byte) (Type.ENUM + pointer);                      // enum
            case 3625364: return (byte) (Type.VOID + pointer);                      // void
            default: return Type.TYPEDEF_TYPE;                                      // vlastný typ
        }
    }

    public byte getTypeCategory() {
        return typeCategory;
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "UnaryOperator");
        if (operator != null) System.out.println(indent + operator);
        if (expression != null) expression.traverse(indent + "    ");
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEnumStructUnion() {
        return false;
    }

    @Override
    public boolean isTypeDeclaration() {
        return false;
    }

    @Override
    public Node getType() {
        return null;
    }

    @Override
    public void addType(Node type) {

    }

    @Override
    public boolean isIdentifierType() {
        return false;
    }
}