package Compiler.AbstractSyntaxTree;

import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;

public class Assignment extends Node {
    Node left;
    Node right;
    String operator;

    public Assignment(Node left, String op, Node right, SymbolTable table) {
        this.left = left;
        this.operator = op;
        this.right = right;

        if (!typeCheck(table)) {
            //TODO: Sémantická chyba
            System.out.println("Sémantická chyba!");
        }
    }

    private boolean typeCheck(SymbolTable table) {
        byte var1 = findTypeKategory(left, table);
        byte var2 = findTypeKategory(right, table);

        return var1 >= var2;
    }

    private byte findTypeKategory(Node left, SymbolTable table) {
        //TODO: môže byť aj Cast ??
        if (left instanceof BinaryOperator) {
            return ((BinaryOperator) left).getTypeCategory();
        } else if (left instanceof Identifier) {
            //nájsť v symbolickej tabuľke
            Record record = table.lookup(((Identifier) left).getName());
            if (record == null) {
                return -1;
            } else {
                return record.getType();
            }
        } else if (left instanceof Constant) {
            // nájsť kategóriu, spraviť funkciu
            //TODO: dokončíť zistenie typu konštanty
            return 0;
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
        } else {
            return -1;
        }
    }

    public byte getLeftType(SymbolTable table) {
        return findTypeKategory(left, table);
    }

    @Override
    public void traverse(String indent) {
        System.out.println(indent + "Assignmnent: ");
        if (left != null) left.traverse(indent + "    ");
        if (operator != null) System.out.println(indent + operator);
        if (right != null) right.traverse(indent + "    ");
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
