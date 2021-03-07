package Compiler.Parser;

import Compiler.AbstractSyntaxTree.Enum;
import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Lexer.Scanner;
import Compiler.Lexer.Token;
import Compiler.Lexer.Tag;
import Compiler.SymbolTable.Kind;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import java.io.IOException;
import java.util.ArrayList;
import Compiler.AbstractSyntaxTree.*;
import Compiler.SymbolTable.Type;

public class Parser {
    private Error err = new Error();
    private int position = 0;
    public ArrayList<Token> tokenStream = new ArrayList<>();
    private Node parseTree;
    private ErrorDatabase errorDatabase;

    public SymbolTable symbolTable = new SymbolTable(null);

    public Parser(String file, ErrorDatabase errorDatabase) {
        this.errorDatabase = errorDatabase;
        Scanner scanner = new Scanner(file, errorDatabase);
        Token tok;
        while (true) {
            try {
                tok = scanner.scan();
                if (tok.tag == Tag.EOF) {
                    break;
                } else {
                    System.out.println(tok.line + ": " + tok.value);
                    tokenStream.add(tok);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void parse() {
        ArrayList<Node> child = translation_unit();
        if (child == null) {
            System.out.println("Chyba v parse tree!");
            parseTree = null;
        } else {
            parseTree = new AST(child);
            parseTree.traverse("");
        }
    }

    private void nextToken() {
        if (position != tokenStream.size() - 1) {
            position++;
        }
    }

    private int getTokenLine() {
        return tokenStream.get(position).line;
    }

    private int getTokenLine(int index) {
        return tokenStream.get(index).line;
    }

    private String getTokenValue() {
        return tokenStream.get(position).value;
    }

    private String getTokenValue(int index) {
        return tokenStream.get(index).value;
    }

    private byte getTokenTag() {
        return tokenStream.get(position).tag;
    }

    private byte getTokenTag(int index) {
        return tokenStream.get(index).tag;
    }

    private Leaf accept(byte tag) {
        if (getTokenTag() == tag) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            return terminal;
        }
        return null;
    }

    private Leaf expect(byte tag) {
        Leaf terminal = accept(tag);
        if (terminal != null) {
            return terminal;
        }
        switch (tag) {
            case Tag.LEFT_BRACKETS:
            case Tag.RIGHT_BRACKETS:
            case Tag.LEFT_PARENTHESES:
            case Tag.RIGHT_PARENTHESES:
            case Tag.LEFT_BRACES:
            case Tag.RIGHT_BRACES:
                System.out.println("Chybajúca zátvorka na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-01"), "E-SxA-01");
                break;
            case Tag.SEMICOLON:
                System.out.println("Chybajúca ; na riadku " + getTokenLine() + "!" + getTokenValue());
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-03"), "E-SxA-03");
                break;
            case Tag.PLUS:
            case Tag.MINUS:
            case Tag.MULT:
            case Tag.DIV:
            case Tag.MOD:
                System.out.println("Chybajúci operátor na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-05"), "E-SxA-05");
                break;
            case Tag.IDENTIFIER:
                if (getTokenTag() < 32) {
                    System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("L-SxA-09"), "L-SxA-09");
                } else {
                    System.out.println("Chybajúci argument na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-06"), "E-SxA-06");
                }
                break;
            default:
                switch (getTokenTag()) {
                    case Tag.LEFT_BRACKETS:
                    case Tag.RIGHT_BRACKETS:
                    case Tag.LEFT_BRACES:
                    case Tag.RIGHT_BRACES:
                    case Tag.LEFT_PARENTHESES:
                    case Tag.RIGHT_PARENTHESES:
                        System.out.println("Zátvorka naviac na riadku " + getTokenLine() + "!");
                        errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-02"), "E-SxA-02");
                        break;
                    default:
                        if (tag < 32 && getTokenTag() == Tag.IDENTIFIER) {
                            System.out.println("Chybné kľúčové slovo na riadku " + getTokenLine() + "!");
                            errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-04"), "E-SxA-04");
                        } else {
                            System.out.println("Chyba na riadku " + getTokenLine() + "!");
                            errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                        }
                }
                break;
        }
        return null;
    }

    /**
     * primary_expression ->  IDENTIFIER
     *                      | constant
     *                      | STRING
     *                      | CHARACTER
     *                      | '(' expression ')'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node primary_expression() {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                Record record = symbolTable.lookup(getTokenValue());
                if (record == null || record.getKind() != Kind.ENUMERATION_CONSTANT) {
                    nextToken();
                    return new Identifier(getTokenValue(position - 1), getTokenLine(position - 1));
                }
                break;
            case Tag.CHARACTER:
                nextToken();
                return new Constant("char", getTokenValue(position - 1), getTokenLine(position - 1));
            case Tag.STRING:
                nextToken();
                return new Constant("string", getTokenValue(position - 1), getTokenLine(position - 1));
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = expression();
                Node child2 = null;
                if (child1 != null && !child1.isNone()) {
                    child2 = accept(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    return child1;
                }
                position = pos;
                symbolTable = copySymbolTable;
                errorDatabase = copyErrorDatabase;
                return new None();
        }
        child1 = constant();
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * constant ->  NUMBER
     *            | REAL
     *            | ENUMERATION_CONSTANT
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node constant() {
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                nextToken();
                return new Constant("enumeration_constant", getTokenValue(position - 1), getTokenLine( position - 1));
            case Tag.NUMBER:
                int u = 0;
                int l = 0;
                for (int i = 0; i < getTokenValue().length(); i++) {
                    if (getTokenValue().charAt(i) == 'l' || getTokenValue().charAt(i) == 'L') {
                        l++;
                    }
                    if (getTokenValue().charAt(i) == 'u' || getTokenValue().charAt(i) == 'U') {
                        u++;
                    }
                }
                if (u > 1 || l > 2) {
                    System.out.println("Error");
                }
                StringBuilder type = new StringBuilder();
                if (u > 0) {
                    type.append("unsigned ");
                }
                while (l > 0) {
                    type.append("long ");
                    l--;
                }
                type.append("int");
                nextToken();
                return new Constant(type.toString(), getTokenValue(position - 1), getTokenLine(position - 1));
            case Tag.REAL:
                String t = "";
                if (getTokenValue().contains("x")) {
                    t = "float";
                } else {
                    if(getTokenValue().contains("f") || getTokenValue().contains("F")) {
                        t = "float";
                    } else if (getTokenValue().contains("l") || getTokenValue().contains("L")) {
                        t = "long double";
                    } else {
                        t = "double";
                    }
                }
                nextToken();
                return new Constant(t, getTokenValue(position - 1), getTokenLine(position - 1));
            default:
                return new None();
        }
    }

    /**
     * enumeration_constant -> IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private String enumeration_constant() {
        if (getTokenTag() == Tag.IDENTIFIER) {
            //vloženie do symbolickej tabuľky ako ENUMERATION_CONSTANT
            //symbolTable.insert(getTokenValue(), "", getTokenLine(), Kind.ENUMERATION_CONSTANT, errorDatabase);

            nextToken();
            return getTokenValue(position - 1);
        }
        return "";
    }

    /**
     * postfix_expression ->  primary_expression rest1
     *                      | '(' type_name ')' '{' initializer_list '}' rest1
     *                      | '(' type_name ')' '{' initializer_list ',' '}' rest1
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node postfix_expression() {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.LEFT_PARENTHESES) {
            nextToken();
            child1 = type_name();
            Node child3 = null, child4 = null, child5;
            if (child1 != null && !child1.isNone()) {
                child2 = accept(Tag.RIGHT_PARENTHESES);
            }
            if (child2 != null) {
                child3 = accept(Tag.LEFT_BRACES);
            }
            if (child3 != null) {
                child4 = initializer_list();
            }
            if (child4 != null && !child4.isNone()) {
                Node ref = null;
                switch (getTokenTag()) {
                    case Tag.RIGHT_BRACES:
                        nextToken();
                        ref = new CompoundLiteral(child1, child4);
                        child5 = rest1(ref);
                        if (child5 != null && !child5.isEmpty()) {
                            return child5;
                        }
                        if (child5 != null && child5.isEmpty()) {
                            return ref;
                        }
                        break;
                    case Tag.COMMA:
                        nextToken();
                        child5 = expect(Tag.RIGHT_BRACES);
                        Node child6 = null;
                        if (child5 != null) {
                            ref = new CompoundLiteral(child1, child4);
                            child6 = rest1(ref);
                        }
                        if (child6 != null && !child6.isEmpty()) {
                            return child6;
                        }
                        if (child6 != null && child6.isEmpty()) {
                            return ref;
                        }
                        break;
                }
            }
            position = pos;
            symbolTable = copySymbolTable;
            errorDatabase = copyErrorDatabase;
        }
        child1 = primary_expression();
        child2 = null;
        if (!child1.isNone()) {
            child2 = rest1(child1);
        }
        if (child2 != null && !child2.isEmpty()) {
            return child2;
        }
        if (child2 != null && child2.isEmpty()) {
            return child1;
        }
        return new None();
    }

    /**
     * rest1 ->  '[' expression ']' rest1
     *         | '.' IDENTIFIER rest1
     *         | '->' IDENTIFIER rest1
     *         | '++' rest1
     *         | '--' rest1
     *         | '(' ')' rest1
     *         | '(' argument_expression_list ')' rest1
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node rest1(Node child) {
        Node child1, child2 = null, child3 = null;
        Node ref = null;
        String terminal;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = expression();
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    ref = new ArrayReference(child, child1, child.getLine());
                    child3 = rest1(ref);
                }
                if (child3 != null && !child3.isEmpty()) {
                    return child3;
                }
                if (child3 != null && child3.isEmpty()) {
                    return ref;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.DOT:
            case Tag.ARROW:
                terminal = getTokenValue();
                nextToken();
                child1 = expect(Tag.IDENTIFIER);
                if (child1 != null) {
                    ref = new StructReference(child, terminal, new Identifier(getTokenValue(position - 1),
                            getTokenLine(position - 1)), child.getLine());
                    child2 = rest1(ref);
                }
                if (child2 != null && !child2.isEmpty()) {
                    return child2;
                }
                if (child2 != null && child2.isEmpty()) {
                    return ref;
                }
                return null;
            case Tag.INC:
            case Tag.DEC:
                terminal = getTokenValue();
                nextToken();
                ref = new UnaryOperator(child, terminal, symbolTable, child.getLine());
                child1 = rest1(ref);
                if (child1 != null && !child1.isEmpty()) {
                    return child1;
                }
                if (child1 != null && child1.isEmpty()) {
                    return ref;
                }
                return null;
            case Tag.LEFT_PARENTHESES:
                nextToken();
                if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
                    nextToken();
                    ref = new FunctionCall(child, null, child.getLine());
                    child1 = rest1(ref);
                    if (child1 != null && !child1.isEmpty()) {
                        return child1;
                    }
                    if (child1 != null && child1.isEmpty()) {
                        return ref;
                    }
                    return null;
                }
                child1 = argument_expression_list();
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    ref = new FunctionCall(child, child1, child.getLine());
                    child3 = rest1(ref);
                }
                if (child3 != null && !child3.isEmpty()) {
                    return child3;
                }
                if (child3 != null && child3.isEmpty()) {
                    return ref;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            default:
                return new EmptyStatement();
        }
    }

    /**
     * argument_expression_list ->  argument_expression_list ',' assignment_expression
     *                            | assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node argument_expression_list() {
        ArrayList<Node> arr = new ArrayList<>();
        Node child1 = assignment_expression();
        if (child1 != null && !child1.isNone()) {
            int line = child1.getLine();
            arr.add(child1);
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                child1 = assignment_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    arr.add(child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return new ExpressionList(arr, line);
        }
        if (child1 != null && child1.isNone()) {
            System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
            errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
        }
        return null;
    }

    /**
     * unary_expression ->  postfix_expression
     *                    | '++' unary_expression
     *                    | '--' unary_expression
     *                    | unary_operator cast_expression
     *                    | SIZEOF '(' type_name ')'
     *                    | SIZEOF '(' unary_expression ')'
     *                    | SIZEOF unary_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašlarest
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node unary_expression() {
        Node child1, child2;
        String terminal;
        switch (getTokenTag()) {
            case Tag.INC:
            case Tag.DEC:
                terminal = getTokenValue();
                nextToken();
                child1 = unary_expression();
                if (child1 != null && !child1.isNone()) {
                    return new UnaryOperator(child1, terminal, symbolTable, child1.getLine());
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.SIZEOF:
                terminal = getTokenValue();
                nextToken();
                if (getTokenTag() == Tag.LEFT_PARENTHESES) {
                    nextToken();
                    child1 = type_name();
                    child2 = null;
                    if (child1 != null && !child1.isNone()) {
                        child2 = expect(Tag.RIGHT_PARENTHESES);
                    }
                    if (child2 != null) {
                        return new UnaryOperator(child1, terminal, symbolTable, child1.getLine());
                    }
                    child1 = unary_expression();
                    if (child1 != null && !child1.isNone()) {
                        child2 = expect(Tag.RIGHT_PARENTHESES);
                    }
                    if (child2 != null) {
                        return new UnaryOperator(child1, terminal, symbolTable, child1.getLine());
                    }
                    if (child1 != null && child1.isNone()) {
                        System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                        errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    }
                    return null;
                } else {
                    child1 = unary_expression();
                    if (child1 == null) {
                        return null;
                    }
                    if (!child1.isNone()) {
                        return new UnaryOperator(child1, terminal, symbolTable, child1.getLine());
                    } else {
                        System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                        errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                        return null;
                    }
                }
        }
        String operator = unary_operator();
        if (!operator.equals("")) {
            child1 = cast_expression();
            if (child1.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return new UnaryOperator(child1, operator, symbolTable, child1.getLine());
            }
        }
        child1 = postfix_expression();
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * unary_operator -> '&' | '*' | '+' | '-' | '~' | '!'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private String unary_operator() {
        switch (getTokenTag()) {
            case Tag.AND:
            case Tag.MULT:
            case Tag.PLUS:
            case Tag.MINUS:
            case Tag.BITWISE_NOT:
            case Tag.LOGICAL_NOT:
                nextToken();
                return getTokenValue(position - 1);
            default:
                return "";
        }
    }

    /**
     * cast_expression ->  unary_expression
     *                   | '(' type_name ')' cast_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node cast_expression() {
        Node child1, child2 = null, child3 = null;
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        if (getTokenTag() == Tag.LEFT_PARENTHESES) {
            int line = getTokenLine();
            nextToken();
            child1 = type_name();
            if (child1 != null && !child1.isNone()) {
                child2 = accept(Tag.RIGHT_PARENTHESES);
            }
            if (child2 != null) {
                child3 = cast_expression();
            }
            if (child3 != null && !child3.isNone()) {
                return new Cast(child1, child3, line);
            }
            position = pos;
            symbolTable = copySymbolTable;
            errorDatabase = copyErrorDatabase;
        }
        child1 = unary_expression();
        if (child1 != null && !child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * multiplicative_expression ->  multiplicative_expression '*' cast_expression
     *                             | multiplicative_expression '/' cast_expression
     *                             | multiplicative_expression '%' cast_expression
     *                             | cast_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node multiplicative_expression() {
        Node child1 = cast_expression();
        Node binOperator;
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.MULT || getTokenTag() == Tag.DIV || getTokenTag() == Tag.MOD) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = cast_expression();
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * additive_expression ->  additive_expression '+' multiplicative_expression
     *                       | additive_expression '-' multiplicative_expression
     *                       | multiplicative_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node additive_expression() {
        Node child1 = multiplicative_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.PLUS || getTokenTag() == Tag.MINUS) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = multiplicative_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * shift_expression ->  shift_expression '<<' additive_expression
     *                    | shift_expression '>>' additive_expression
     *                    | additive_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node shift_expression() {
        Node child1 = additive_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.LEFT_SHIFT || getTokenTag() == Tag.RIGHT_SHIFT) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = additive_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * relational_expression ->  relational_expression '<' shift_expression
     *                         | relational_expression '>' shift_expression
     *                         | relational_expression '<=' shift_expression
     *                         | relational_expression '>=' shift_expression
     *                         | shift_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node relational_expression() {
        Node child1 = shift_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.LT || getTokenTag() == Tag.GT || getTokenTag() == Tag.LEQT || getTokenTag() == Tag.GEQT) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = shift_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * equality_expression ->  equality_expression '==' relational_expression
     *                       | equality_expression '!=' relational_expression
     *                       | relational_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node equality_expression() {
        Node child1 = relational_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.EQ || getTokenTag() == Tag.NOT_EQ) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = relational_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * and_expression ->  and_expression '&' equality_expression
     *                  | equality_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node and_expression() {
        Node child1 = equality_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.AND) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = equality_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * exclusive_or_expression ->  exclusive_or_expression '^' and_expression
     *                           | and_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node exclusive_or_expression() {
        Node child1 = and_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.XOR) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = and_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * inclusive_or_expression ->  inclusive_or_expression '|' exclusive_or_expression
     *                           | exclusive_or_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node inclusive_or_expression() {
        Node child1 = exclusive_or_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.OR) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = exclusive_or_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * logical_and_expression ->  logical_and_expression '&&' inclusive_or_expression
     *                          | inclusive_or_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node logical_and_expression() {
        Node child1 = inclusive_or_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.LOGICAL_AND) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = inclusive_or_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * logical_or_expression ->  logical_or_expression '||' logical_and_expression
     *                         | logical_and_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node logical_or_expression() {
        Node child1 = logical_and_expression();
        Node binOperator;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            while (getTokenTag() == Tag.LOGICAL_OR) {
                String terminal = getTokenValue();
                nextToken();
                binOperator = child1;
                child1 = logical_and_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    child1 = new BinaryOperator(binOperator, terminal, child1, symbolTable, binOperator.getLine());
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * conditional_expression ->  logical_or_expression
     *                          | logical_or_expression '?' expression ':' conditional_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node conditional_expression() {
        Node child1 = logical_or_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            if (getTokenTag() == Tag.QMARK) {
                nextToken();
                Node child2 = expression();
                Node child3 = null, child4 = null;
                if (child2 != null && !child2.isNone()) {
                    child3 = expect(Tag.COLON);
                }
                if (child3 != null) {
                    child4 = conditional_expression();
                }
                if (child4 != null && !child4.isNone()) {
                    return new TernaryOperator(child1, child2, child4, symbolTable, child1.getLine());
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                return child1;
            }
        }
        return new None();
    }

    /**
     * assignment_expression ->  conditional_expression
     *                         | unary_expression assignment_operator assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node assignment_expression() {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1 = unary_expression();
        String operator = "";
        Node child2;
        if (child1 != null && !child1.isNone()) {
            operator = assignment_operator();
        }
        if (!operator.equals("")) {
            child2 = assignment_expression();
            if (child2 == null)  {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return new Assignment(child1, operator, child2, symbolTable, child1.getLine());
            }
        }
        position = pos;
        symbolTable = copySymbolTable;
        errorDatabase = copyErrorDatabase;
        child1 = conditional_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * assignment_operator -> '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<' | '>>=' | '&=' | '^=' | '|='
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private String assignment_operator() {
        Node child1 = accept(Tag.ASSIGNMENT);
        if (child1 != null) {
            return getTokenValue(position - 1);
        }
        return "";
    }

    /**
     * expression ->  expression ',' assignment_expression
     *              | assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node expression() {
        Node child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            if (getTokenTag() != Tag.COMMA) {
                return child1;
            }
            int line = child1.getLine();
            ArrayList<Node> arr = new ArrayList<>();
            arr.add(child1);
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                child1 = assignment_expression();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    arr.add(child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return new ExpressionList(arr, line);
        }
        return new None();
    }

    /**
     * constant_expression -> conditional_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node constant_expression() {
        Node child1 = conditional_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * declaration ->  declaration_specifiers ';'
     *               | declaration_specifiers init_declarator_list ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private ArrayList<Node> declaration() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = declaration_specifiers(typeNode);
        if (child1 == null) {
            //error recovery
            while (getTokenTag() != Tag.SEMICOLON) {
                nextToken();
                //ak je koniec súboru
                if (position == tokenStream.size() - 1) {
                    return null;
                }
            }
            nextToken();
            ArrayList<Node> arr = new ArrayList<>();
            arr.add(new Err());
            return arr;
        }
        if (!child1.isNone()) {
            typeNode = (TypeNode) child1;
            if (getTokenTag() == Tag.SEMICOLON) {
                nextToken();
                if (typeNode.getTypes().size() == 1 && typeNode.getType(0).isEnumStructUnion()) {
                    ArrayList<Node> decls = new ArrayList<>();
                    decls.add(new Declaration(null, typeNode.getQualifiers(), typeNode.getStorage(), typeNode.getType(0),
                            null, null, 0));
                    return decls;
                } else {
                    ArrayList<Node> arr = new ArrayList<>();
                    arr.add(new Declarator(null, null));
                    return createDeclaration(typeNode, arr);
                }
            }
            ArrayList<Node> child2 = init_declarator_list();
            Node child3 = null;
            if (child2 == null) {
                //error recovery
                while (getTokenTag() != Tag.SEMICOLON) {
                    nextToken();
                    //ak je koniec súboru
                    if (position == tokenStream.size() - 1) {
                        return null;
                    }
                }
                nextToken();
                ArrayList<Node> arr = new ArrayList<>();
                arr.add(new Err());
                return arr;
            }
            if (!child2.isEmpty()) {
                child3 = expect(Tag.SEMICOLON);
            }
            if (child3 != null) {
                return createDeclaration(typeNode, child2);
            }
            //error recovery
            while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                nextToken();
                //ak je koniec súboru
                if (position == tokenStream.size() - 1) {
                    return null;
                }
            }
            if (getTokenTag() == Tag.SEMICOLON) {
                nextToken();
                ArrayList<Node> arr = new ArrayList<>();
                arr.add(new Err());
                return arr;
            } else {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            }
        }
        return new ArrayList<>();
    }

    /**
     * declaration_specifiers ->  storage_class_specifier declaration_specifiers
     *                          | storage_class_specifier
     *                          | type_specifier declaration_specifiers
     *                          | type_specifier
     *                          | type_qualifier declaration_specifiers
     *                          | type_qualifier
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node declaration_specifiers(TypeNode typeNode) {
        String child1 = storage_class_specifier();
        Node child2;
        if (!child1.equals("")) {
            typeNode.addStorage(child1);
            child2 = declaration_specifiers(typeNode);
            if (child2 == null) {
                return null;
            }
            if (!child2.isNone()) {
                return child2;
            } else {
                return typeNode;
            }
        }
        child2 = type_specifier();
        Node child3;
        if (child2 == null) {
            return null;
        }
        if (!child2.isNone()) {
            typeNode.addType(child2);
            child3 = declaration_specifiers(typeNode);
            if (child3 == null) {
                return null;
            }
            if (!child3.isNone()) {
                return child3;
            } else {
                return typeNode;
            }
        }
        child1 = type_qualifier();
        if (!child1.equals("")) {
            typeNode.addQualifier(child1);
            child2 = declaration_specifiers(typeNode);
            if (child2 == null) {
                return null;
            }
            if (!child2.isNone()) {
                return child2;
            } else {
                return typeNode;
            }
        }
        return new None();
    }

    /**
     * init_declarator_list -> init_declarator_list ',' init_declarator
     *                       | init_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> init_declarator_list() {
        ArrayList<Node> arr = new ArrayList<>();
        Node child1 = init_declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            arr.add(child1);
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                child1 = init_declarator();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    arr.add(child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * init_declarator ->  declarator '=' initializer
     *                   | declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node init_declarator() {
        Node child1 = declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            if (getTokenValue().equals("=")) {
                nextToken();
                Node child2 = initializer();
                if (child2 != null && !child2.isNone()) {
                    return new InitDeclarator(child1, child2);
                }
                if (child2 != null && child2.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                if (getTokenValue().equals("==")) {
                    System.out.println("Využitie '==' namiesto '=' na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
                return new InitDeclarator(child1, null);
            }
        }
        return new None();
    }

    /**
     * storage_class_specifier -> TYPEDEF | EXTERN | STATIC | AUTO | REGISTER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private String storage_class_specifier() {
        switch (getTokenTag()) {
            case Tag.TYPEDEF:
            case Tag.EXTERN:
            case Tag.STATIC:
            case Tag.AUTO:
            case Tag.REGISTER:

                nextToken();
                return getTokenValue(position - 1);
        }
        return "";
    }

    /**
     * type_specifier ->  VOID | CHAR | SHORT | INT | LONG | FLOAT | DOUBLE | SIGNED | UNSIGNED | SIZE_T | FILE
     *                  | struct_or_union_specifier
     *                  | enum_specifier
     *                  | TYPEDEF_NAME
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node type_specifier() {
        ArrayList<String> arr = new ArrayList<>();
        switch (getTokenTag()) {
            case Tag.VOID:
            case Tag.CHAR:
            case Tag.SHORT:
            case Tag.INT:
            case Tag.LONG:
            case Tag.FLOAT:
            case Tag.DOUBLE:
            case Tag.SIGNED:
            case Tag.UNSIGNED:
            case Tag.SIZE_T:
            case Tag.FILE:

                nextToken();
                arr.add(getTokenValue(position - 1));
                return new IdentifierType(arr, getTokenLine(position - 1));
            case Tag.IDENTIFIER:
                //riešenie TYPEDEF_NAME
                Record record = symbolTable.lookup(getTokenValue());
                if (record != null) {
                    if (record.getKind() == Kind.TYPEDEF_NAME) {

                        nextToken();
                        arr.add(getTokenValue(position - 1));
                        return new IdentifierType(arr, getTokenLine(position - 1));
                    }
                }
                return new None();
        }
        Node child1 = struct_or_union_specifier();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        child1 = enum_specifier();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * struct_or_union_specifier ->  struct_or_union '{' struct_declaration_list '}'
     *                             | struct_or_union IDENTIFIER '{' struct_declaration_list '}'
     *                             | struct_or_union IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node struct_or_union_specifier() {
        String child1 = struct_or_union();
        ArrayList<Node> child2;
        Node child3 = null;
        int line;
        if (!child1.equals("")) {
            switch (getTokenTag()) {
                case Tag.LEFT_BRACES:
                    line = getTokenLine();
                    nextToken();
                    child2 = struct_declaration_list();
                    if (child2 == null) {
                        //error recovery
                        while (getTokenTag() != Tag.RIGHT_BRACES) {
                            nextToken();
                            //ak je koniec súboru
                            if (position == tokenStream.size() - 1) {
                                return null;
                            }
                        }
                        nextToken();
                        return new Err();
                    }
                    if (!child2.isEmpty()) {
                        child3 = expect(Tag.RIGHT_BRACES);
                    }
                    if (child3 != null) {
                        if (child1.equals("struct")) {
                            return new Struct(null, child2, line);
                        } else {
                            return new Union(null, child2, line);
                        }
                    } else {
                        //error recovery
                        while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                            nextToken();
                            //ak je koniec súboru
                            if (position == tokenStream.size() - 1) {
                                return null;
                            }
                        }
                        if (getTokenTag() == Tag.RIGHT_BRACES) {
                            nextToken();
                            return new Err();
                        } else {
                            return null;
                        }
                    }
                case Tag.IDENTIFIER:
                    String terminal = getTokenValue();
                    line = getTokenLine();
                    nextToken();
                    if (getTokenTag() == Tag.LEFT_BRACES) {
                        nextToken();
                        child2 = struct_declaration_list();
                        if (child2 == null) {
                            //error recovery
                            while (getTokenTag() != Tag.RIGHT_BRACES) {
                                nextToken();
                                //ak je koniec súboru
                                if (position == tokenStream.size() - 1) {
                                    return null;
                                }
                            }
                            nextToken();
                            return new Err();
                        }
                        if (!child2.isEmpty()) {
                            child3 = expect(Tag.RIGHT_BRACES);
                        }
                        if (child3 != null) {
                            if (child1.equals("struct")) {
                                return new Struct(terminal, child2, line);
                            } else {
                                return new Union(terminal, child2, line);
                            }
                        } else {
                            //error recovery
                            while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                                nextToken();
                                //ak je koniec súboru
                                if (position == tokenStream.size() - 1) {
                                    return null;
                                }
                            }
                            if (getTokenTag() == Tag.RIGHT_BRACES) {
                                nextToken();
                                return new Err();
                            } else {
                                return null;
                            }
                        }
                    } else {
                        if (child1.equals("struct")) {
                            return new Struct(terminal, null, line);
                        } else {
                            return new Union(terminal, null, line);
                        }
                    }
            }
            if (getTokenTag() < 32) {
                System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("L-SxA-09"), "L-SxA-09");
                return null;
            }
        }
        return new None();
    }

    /**
     * struct_or_union -> STRUCT | UNION
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private String struct_or_union() {
        switch (getTokenTag()) {
            case Tag.STRUCT:
            case Tag.UNION:

                nextToken();
                return getTokenValue(position - 1);
        }
        return "";
    }

    /**
     * struct_declaration_list ->  struct_declaration_list struct_declaration
     *                           | struct_declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> struct_declaration_list() {
        ArrayList<Node> child1 = struct_declaration();
        if (child1 == null) {
            return null;
        }
        ArrayList<Node> decl = new ArrayList<>(child1);
        if (!child1.isEmpty()) {
            child1 = struct_declaration();
            if (child1 == null) {
                return null;
            }
            while (!child1.isEmpty()) {
                decl.addAll(child1);
                child1 = struct_declaration();
                if (child1 == null) {
                    return null;
                }
            }
            return decl;
        }
        return new ArrayList<>();
    }

    /**
     * struct_declaration ->  specifier_qualifier_list ';'
     *                      | specifier_qualifier_list struct_declarator_list ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private ArrayList<Node> struct_declaration() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = specifier_qualifier_list(typeNode);
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            typeNode = (TypeNode) child1;
            if (getTokenTag() == Tag.SEMICOLON) {
                nextToken();
                ArrayList<Node> arr = new ArrayList<>();
                if (typeNode.getTypes().size() == 1) {
                    arr.add(new Declarator(typeNode.getType(0), null));
                } else {
                    arr.add(new Declarator(null, null));
                }
                return createDeclaration(typeNode, arr, true);
            }
            ArrayList<Node> child2 = struct_declarator_list();
            Node child3;
            if (child2 == null) {
                //error recovery
                while (getTokenTag() != Tag.SEMICOLON) {
                    nextToken();
                    //ak je koniec súboru
                    if (position == tokenStream.size() - 1) {
                        return null;
                    }
                }
                nextToken();
                ArrayList<Node> arr = new ArrayList<>();
                arr.add(new Err());
                return arr;
            }
            if (!child2.isEmpty()) {
                child3 = expect(Tag.SEMICOLON);
                if (child3 == null) {
                    // error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    if (getTokenTag() == Tag.SEMICOLON) {
                        nextToken();
                        ArrayList<Node> arr = new ArrayList<>();
                        arr.add(new Err());
                        return arr;
                    } else {
                        return null;
                    }
                } else {
                    return createDeclaration(typeNode, child2, true);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * specifier_qualifier_list ->  type_specifier specifier_qualifier_list
     *                            | type_specifier
     *                            | type_qualifier specifier_qualifier_list
     *                            | type_qualifier
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node specifier_qualifier_list(TypeNode typeNode) {
        Node child1 = type_specifier();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            typeNode.addType(child1);
            child2 = specifier_qualifier_list(typeNode);
            if (child2 == null) {
                return null;
            }
            if (!child2.isNone()) {
                return child2;
            } else {
                return typeNode;
            }
        }
        String child = type_qualifier();
        if (!child.equals("")) {
            typeNode.addQualifier(child);
            child2 = specifier_qualifier_list(typeNode);
            if (child2 == null) {
                return null;
            }
            if (!child2.isNone()) {
                return child2;
            } else {
                return typeNode;
            }
        }
        return new None();
    }

    /**
     * struct_declarator_list ->  struct_declarator_list ',' struct_declarator
     *                          | struct_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> struct_declarator_list() {
        ArrayList<Node> arr = new ArrayList<>();
        Node child1 = struct_declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            arr.add(child1);
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                child1 = struct_declarator();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    arr.add(child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * struct_declarator ->  ':' constant_expression
     *                     | declarator ':' constant_expression
     *                     | declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node struct_declarator() {
        Node child1;
        if (getTokenTag() == Tag.COLON) {
            nextToken();
            child1 = constant_expression();
            if (child1 != null && !child1.isNone()) {
                return new StructDeclarator(new TypeDeclaration(null, null, null), child1);
            }
            if (child1 != null && child1.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
            }
            return null;
        }
        child1 = declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            if (getTokenTag() == Tag.COLON) {
                nextToken();
                Node child2 = constant_expression();
                if (child2 != null && !child2.isNone()) {
                    return new StructDeclarator(child1, child2);
                }
                if (child2 != null && child2.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                return new StructDeclarator(child1, null);
            }
        }
        return new None();
    }

    /**
     * enum_specifier -> ENUM left13
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node enum_specifier() {
        if (getTokenTag() == Tag.ENUM) {
            nextToken();
            Node child1 = left13(getTokenLine(position - 1));
            if (child1 != null && !child1.isNone()) {
                return child1;
            }
            if (child1 != null && child1.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
            }
            return null;
        }
        return new None();
    }

    /**
     * left13 ->  '{' enumerator_list '}'
     *          | '{' enumerator_list ',' '}'
     *          | IDENTIFIER '{' enumerator_list '}'
     *          | IDENTIFIER '{' enumerator_list ',' '}'
     *          | IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left13(int line) {
        Node child1, child2, child3;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACES:
                nextToken();
                child1 = enumerator_list();
                if (child1 != null && !child1.isNone()) {
                    switch (getTokenTag()) {
                        case Tag.RIGHT_BRACES:
                            nextToken();
                            return new Enum(null, child1, line);
                        case Tag.COMMA:
                            nextToken();
                            child2 = expect(Tag.RIGHT_BRACES);
                            if (child2 != null) {
                                return new Enum(null, child1, line);
                            }
                    }
                }
                //error recovery
                while (getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                    //ak je koniec súboru
                    if (position == tokenStream.size() - 1) {
                        return null;
                    }
                }
                nextToken();
                return new Err();
            case Tag.IDENTIFIER:
                String terminal = getTokenValue();
                nextToken();
                if (getTokenTag() == Tag.LEFT_BRACES) {
                    nextToken();
                    child1 = enumerator_list();
                    if (child1 != null && !child1.isNone()) {
                        switch (getTokenTag()) {
                            case Tag.RIGHT_BRACES:
                                nextToken();
                                return new Enum(terminal, child1, line);
                            case Tag.COMMA:
                                nextToken();
                                child3 = expect(Tag.RIGHT_BRACES);
                                if (child3 != null) {
                                    return new Enum(terminal, child1, line);
                                }
                        }
                    }
                    //error recovery
                    while (getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    nextToken();
                    return new Err();
                } else {
                    return new Enum(terminal, null, line);
                }
        }
        if (getTokenTag() < 32) {
            System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
            errorDatabase.addErrorMessage(getTokenLine(), err.getError("L-SxA-09"), "L-SxA-09");
            return null;
        }
        return new None();
    }

    /**
     * enumerator_list ->  enumerator_list ',' enumerator
     *                   | enumerator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node enumerator_list() {
        Node child1 = enumerator();
        if (child1 == null) {
            return null;
        }
        ArrayList<Node> arr = new ArrayList<>();
        if (!child1.isNone()) {
            int line = child1.getLine();
            arr.add(child1);
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                child1 = enumerator();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    arr.add(child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return new EnumeratorList(arr, line);
        }
        return new None();
    }

    /**
     * enumerator ->  enumeration_constant '=' constant_expression
     *              | enumeration_constant
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node enumerator() {
        String child1 = enumeration_constant();
        int line = getTokenLine(position - 1);
        if (!child1.equals("")) {
            if (getTokenValue().equals("=")) {
                nextToken();
                Node child2 = constant_expression();
                if (child2 != null && !child2.isNone()) {
                    return new Enumerator(child1, child2, line);
                }
                if (child2 != null && child2.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                return new Enumerator(child1, null, line);
            }
        }
        return new None();
    }

    /**
     * type_qualifier -> CONST | VOLATILE
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private String type_qualifier() {
        switch (getTokenTag()) {
            case Tag.CONST:
            case Tag.VOLATILE:

                nextToken();
                return getTokenValue(position - 1);
        }
        return "";
    }

    /**
     * declarator ->  pointer direct_declarator
     *              | direct_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node declarator() {
        Node child1 = pointer();
        Node child2;
        if (!child1.isNone()) {
            child2 = direct_declarator();
            if (child2 != null && !child2.isNone()) {
                return modifyType(child2, child1);
            }
            return new None();
        }
        child1 = direct_declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * direct_declarator ->  IDENTIFIER rest18
     *                     | '(' declarator ')' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node direct_declarator() {
        Node child1, child2 = null, child3 = null;
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                String terminal = getTokenValue();
                nextToken();
                Node declarator = new TypeDeclaration(terminal, null, null);
                child1 = rest18(declarator);
                if (child1 == null) {
                    return null;
                }
                if (child1.isEmpty()) {
                    return declarator;
                } else {
                    return child1;
                }
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = declarator();
                if (child1 != null && !child1.isNone()) {
                    child2 = accept(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    child3 = rest18(child1);
                }
                if (child3 != null && !child3.isEmpty()) {
                    return child3;
                }
                if (child3 != null && child3.isEmpty()) {
                    return child1;
                }
                position = pos;
                symbolTable = copySymbolTable;
                errorDatabase = copyErrorDatabase;
                return new None();
        }
        //TODO: nie som si istý
        if (getTokenTag() < 32) {
            System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
            errorDatabase.addErrorMessage(getTokenLine(), err.getError("L-SxA-09"), "L-SxA-09");
            return null;
        }
        return new None();
    }

    /**
     * rest18 ->  '[' left16
     *          | '(' left17
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node rest18(Node declarator) {
        Node child1;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = left16(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = left17(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            default:
                return new EmptyStatement();
        }
    }

    /**
     * left16 ->  '*' ']' rest18
     *          | STATIC left18
     *          | type_qualifier_list left19
     *          | assignment_expression ']' rest18
     *          | ']' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left16(Node declarator) {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) symbolTable.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1, child2;
        Node decl;
        switch (getTokenTag()) {
            case Tag.MULT:
                nextToken();
                child1 = accept(Tag.RIGHT_BRACKETS);
                if (child1 != null) {
                    decl = new ArrayDeclaration(null, new Identifier("*", getTokenLine(position - 1)),
                            new ArrayList<>(), getTokenLine(position - 1));
                    Node child = modifyType(declarator, decl);
                    child2 = rest18(child);
                    if (child2 == null) {
                        return null;
                    }
                    if (!child2.isEmpty()) {
                        return child2;
                    } else {
                        return child;
                    }
                } else {
                    position = pos;
                    symbolTable = copySymbolTable;
                    errorDatabase = copyErrorDatabase;
                    break;
                }
            case Tag.STATIC:
                nextToken();
                child1 = left18(declarator);
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    return child1;
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            case Tag.RIGHT_BRACKETS:
                nextToken();
                decl = new ArrayDeclaration(null, null, new ArrayList<>(), getTokenLine(position - 1));
                Node child = modifyType(declarator, decl);
                child1 = rest18(child);
                if (child1 == null) {
                    return null;
                }
                if (!child1.isEmpty()) {
                    return child1;
                } else {
                    return child;
                }
        }
        Node child3;
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                decl = new ArrayDeclaration(null, child1, new ArrayList<>(), getTokenLine(position - 1));
                Node child = modifyType(declarator, decl);
                child3 = rest18(child);
                if (child3 == null ) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child;
                }
            }
        }
        ArrayList<String> child4 = type_qualifier_list();
        if (!child4.isEmpty()) {
            child2 = left19(child4, declarator);
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return child2;
            }
        }
        return new None();
    }

    /**
     * left17 ->  parameter_type_list ')' rest18
     *          | ')' rest18
     *          | identifier_list rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left17(Node declarator) {
        Node child1, decl, child;
        if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
            nextToken();
            decl = new FunctionDeclaration(null, null, getTokenLine(position - 1));
            child = modifyType(declarator, decl);
            child1 = rest18(child);
            if (child1 == null) {
                return null;
            }
            if (!child1.isEmpty()) {
                return child1;
            } else {
                return child;
            }
        }
        child1 = parameter_type_list();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                decl = new FunctionDeclaration(child1, null, getTokenLine(position - 1));
                child = modifyType(declarator, decl);
                child3 = rest18(child);
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child;
                }
            }
        }
        child1 = identifier_list();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            decl = new FunctionDeclaration(child1, null, child1.getLine());
            child = modifyType(declarator, decl);
            child2 = rest18(child);
            if (child2 == null) {
                return null;
            }
            if (!child2.isEmpty()) {
                return child2;
            } else {
                return child;
            }
        }
        return new None();
    }

    /**
     * left18 ->  type_qualifier_list assignment_expression ']' rest18
     *          | assignment_expression ']' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left18(Node declarator) {
        ArrayList<String> child1 = type_qualifier_list();
        Node decl, child2, child3, child4;
        if (!child1.isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                child3 = expect(Tag.RIGHT_BRACKETS);
                if (child3 == null) {
                    return null;
                } else {
                    //pridanie static medzi qualifiers na začiatok
                    child1.add(0, "static");

                    decl = new ArrayDeclaration(null, child2, child1, getTokenLine(position - 1));
                    Node child = modifyType(declarator, decl);
                    child4 = rest18(child);
                    if (child4 == null) {
                        return null;
                    }
                    if (!child4.isEmpty()) {
                        return child4;
                    } else {
                        return child;
                    }
                }
            }
        }
        child2 = assignment_expression();
        if (child2 == null) {
            return null;
        }
        if (!child2.isNone()) {
            child3 = expect(Tag.RIGHT_BRACKETS);
            if (child3 == null) {
                return null;
            } else {
                child1 = new ArrayList<>();
                child1.add("static");

                decl = new ArrayDeclaration(null, child2, child1, getTokenLine(position - 1));
                Node child = modifyType(declarator, decl);
                child4 = rest18(child);
                if (child4 == null) {
                    return null;
                }
                if (!child4.isEmpty()) {
                    return child4;
                } else {
                    return child;
                }
            }
        }
        return new None();
    }

    /**
     * left19 ->  '*' ']' rest18
     *          | STATIC assignment_expression ']' rest18
     *          | assignment_expression ']' rest18
     *          | ']' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left19(ArrayList<String> qualifiers, Node declarator) {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1, child2 = null, child3 = null;
        Node decl, child = null;
        switch (getTokenTag()) {
            case Tag.MULT:
                nextToken();
                child1 = accept(Tag.RIGHT_BRACKETS);
                if (child1 != null) {
                    decl = new ArrayDeclaration(null, new Identifier("*", getTokenLine(position - 1)),
                            qualifiers, getTokenLine(position - 1));
                    child = modifyType(declarator, decl);
                    child2 = rest18(child);
                    if (child2 == null) {
                        return null;
                    }
                    if (!child2.isEmpty()) {
                        return child2;
                    } else {
                        return child;
                    }
                } else {
                    position = pos;
                    symbolTable = copySymbolTable;
                    errorDatabase = copyErrorDatabase;
                    break;
                }
            case Tag.STATIC:
                nextToken();
                child1 = assignment_expression();
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    qualifiers.add("static");

                    decl = new ArrayDeclaration(null, child1, qualifiers, getTokenLine(position - 1));
                    child = modifyType(declarator, decl);
                    child3 = rest18(child);
                }
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child;
                }
            case Tag.RIGHT_BRACKETS:
                nextToken();
                decl = new ArrayDeclaration(null, null, qualifiers, getTokenLine(position - 1));
                child = modifyType(declarator, decl);
                child1 = rest18(child);
                if (child1 == null) {
                    return null;
                }
                if (!child1.isEmpty()) {
                    return child1;
                } else {
                    return child;
                }
        }
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                decl = new ArrayDeclaration(null, child1, qualifiers, getTokenLine(position - 1));
                child = modifyType(declarator, decl);
                child3 = rest18(child);
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else  {
                    return child;
                }
            }
        }
        return new None();
    }

    /**
     * pointer ->  '*' type_qualifier_list pointer
     *           | '*' type_qualifier_list
     *           | '*' pointer
     *           | '*'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node pointer() {
        if (getTokenTag() == Tag.MULT) {
            int line = getTokenLine();
            nextToken();
            ArrayList<String> child1 = type_qualifier_list();
            Node child2;
            if (!child1.isEmpty()) {
                child2 = pointer();
                if (!child2.isNone()) {
                    Node tail = child2;
                    while (tail.getType() != null) {
                        tail = tail.getType();
                    }

                    tail.addType(new PointerDeclaration(child1, null, line));
                    return child2;
                } else {
                    return new PointerDeclaration(child1, null, line);
                }
            }
            child2 = pointer();
            if (!child2.isNone()) {
                Node tail = child2;
                while (tail.getType() != null) {
                    tail = tail.getType();
                }

                tail.addType(new PointerDeclaration(new ArrayList<>(), null, line));
                return child2;
            } else {
                return new PointerDeclaration(new ArrayList<>(), null, line);
            }
        }
        return new None();
    }

    /**
     * type_qualifier_list ->  type_qualifier_list type_qualifier
     *                       | type_qualifier
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<String> type_qualifier_list() {
        String child1 = type_qualifier();
        ArrayList<String> arr = new ArrayList<>();
        if (!child1.equals("")) {
            arr.add(child1);
            child1 = type_qualifier();
            while (!child1.equals("")) {
                arr.add(child1);
                child1 = type_qualifier();
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * parameter_type_list ->  parameter_list ',' '...'
     *                       | parameter_list
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node parameter_type_list() {
        ParameterList child1 = parameter_list();
        if (child1 == null) {
            return null;
        }
        if (!child1.getParameters().isEmpty()) {
            if (getTokenTag() == Tag.COMMA) {
                nextToken();
                Node child2 = expect(Tag.ELLIPSIS);
                if (child2 != null) {
                    child1.addParameter(new EllipsisParam());
                    return child1;
                }
                return null;
            } else {
                return child1;
            }
        }
        return new None();
    }

    /**
     * parameter_list ->  parameter_list ',' parameter_declaration
     *                  | parameter_declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ParameterList parameter_list() {
        Node child1 = parameter_declaration();
        if (child1 == null) {
            return null;
        }
        ArrayList<Node> arr = new ArrayList<>();
        if (!child1.isNone()) {
            int line = child1.getLine();
            arr.add(child1);
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                child1 = parameter_declaration();
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    arr.add(child1);
                } else {
                   position--;
                   return new ParameterList(arr, line);
                }
            }
            return new ParameterList(arr, line);
        }
        return new ParameterList(arr, 0);
    }

    /**
     * parameter_declaration -> declaration_specifiers left22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node parameter_declaration() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = declaration_specifiers(typeNode);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            typeNode = (TypeNode) child1;
            child2 = left22(typeNode);
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return child2;
            }
        }
        return new None();
    }

    /**
     * left22 ->  declarator
     *          | abstract_declarator
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left22(TypeNode typeNode) {
        if (typeNode.getTypes().isEmpty()) {
            ArrayList<String> arr = new ArrayList<>();
            arr.add("int");
            typeNode.addType(new IdentifierType(arr, 0));
        }

        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1 = declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            ArrayList<Node> decls = new ArrayList<>();
            decls.add(new Declarator(child1, null));
            return createDeclaration(typeNode, decls, true, false).get(0);
        }
        position = pos;
        symbolTable = copySymbolTable;
        errorDatabase = copyErrorDatabase;
        child1 = abstract_declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            if (typeNode.getTypes().size() > 1) {
                ArrayList<Node> decls = new ArrayList<>();
                decls.add(new Declarator(child1, null));
                return createDeclaration(typeNode, decls, true, false).get(0);
            } else {
                Node decl = new Typename("", typeNode.getQualifiers(), child1, child1.getLine());
                decl = fixTypes(decl, typeNode.getTypes());
                return decl;
            }
        }
        if (typeNode.getTypes().size() > 1) {
            ArrayList<Node> decls = new ArrayList<>();
            decls.add(new Declarator(null, null));
            return createDeclaration(typeNode, decls, true, false).get(0);
        } else {
            Node decl = new Typename("", typeNode.getQualifiers(), new TypeDeclaration(null, null,
                    null), 0);
            decl = fixTypes(decl, typeNode.getTypes());
            return decl;
        }
    }

    /**
     * identifier_list ->  identifier_list ',' IDENTIFIER
     *                   | IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node identifier_list() {
        ArrayList<Node> arr = new ArrayList<>();
        if (getTokenTag() == Tag.IDENTIFIER) {
            int line = getTokenLine();
            arr.add(new Identifier(getTokenValue(), getTokenLine()));
            nextToken();
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                if (getTokenTag() == Tag.IDENTIFIER) {
                    arr.add(new Identifier(getTokenValue(), getTokenLine()));
                    nextToken();
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return new ParameterList(arr, line);
        }
        if (getTokenTag() < 32) {
            System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
            errorDatabase.addErrorMessage(getTokenLine(), err.getError("L-SxA-09"), "L-SxA-09");
            return null;
        }
        return new None();
    }

    /**
     * type_name ->  specifier_qualifier_list abstract_declarator
     *             | specifier_qualifier_list
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node type_name() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = specifier_qualifier_list(typeNode);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            typeNode = (TypeNode) child1;
            child2 = abstract_declarator();
            if (child2 == null) {
                return null;
            }
            if (!child2.isNone()) {
                return fixTypes(new Typename("", typeNode.getQualifiers(), child2, child2.getLine()), typeNode.getTypes());
            } else {
                return fixTypes(new Typename("", typeNode.getQualifiers(), new TypeDeclaration(null,
                        null, null), 0), typeNode.getTypes());
            }
        }
        return new None();
    }

    /**
     * abstract_declarator ->  pointer direct_abstract_declarator
     *                       | pointer
     *                       | direct_abstract_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node abstract_declarator() {
        Node child1 = pointer();
        Node child2;
        if (!child1.isNone()) {
            child2 = direct_abstract_declarator();
            if (child2 == null) {
                return null;
            }
            if (!child2.isNone()) {
                return modifyType(child2, child1);
            } else {
                return modifyType(new TypeDeclaration(null, null, null), child1);
            }
        }
        child1 = direct_abstract_declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * direct_abstract_declarator ->  '(' left25
     *                              | '[' left26
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node direct_abstract_declarator() {
        Node child1;
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = left25();
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                position = pos;
                symbolTable = copySymbolTable;
                errorDatabase = copyErrorDatabase;
                return new None();
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = left26(null);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * left25 ->  abstract_declarator ')' rest22
     *          | ')' rest22
     *          | parameter_type_list ')' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left25() {
        Node child1, decl;
        if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
            nextToken();
            decl = new FunctionDeclaration(null, null, getTokenLine(position - 1));
            child1 = rest22(decl);
            if (child1 == null) {
                return null;
            }
            if (!child1.isEmpty()) {
                return child1;
            } else {
                return decl;
            }
        }
        child1 = abstract_declarator();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22(child1);
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child1;
                }
            }
        }
        child1 = parameter_type_list();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                decl = new FunctionDeclaration(child1, null, getTokenLine(position - 1));
                child3 = rest22(decl);
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return decl;
                }
            }
        }
        return new None();
    }

    /**
     * left26 ->  ']' rest22
     *          | '*' ']' rest22
     *          | STATIC left27
     *          | type_qualifier_list left28
     *          | assignment_expression ']' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left26(Node declarator) {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1, child2;
        Node decl, child;
        switch (getTokenTag()) {
            case Tag.RIGHT_BRACKETS:
                nextToken();
                if (declarator != null) {
                    decl = new ArrayDeclaration(null, null, new ArrayList<>(), getTokenLine(position - 1));
                    child = modifyType(declarator, decl);
                } else {
                    child = new ArrayDeclaration(new TypeDeclaration(null, null, null), null,
                            new ArrayList<>(), getTokenLine(position - 1));
                }
                child1 = rest22(child);
                if (child1 == null) {
                    return null;
                }
                if (!child1.isEmpty()) {
                    return child1;
                } else {
                    return child;
                }
            case Tag.MULT:
                nextToken();
                child1 = accept(Tag.RIGHT_BRACKETS);
                if (child1 != null) {
                    if (declarator != null) {
                        decl = new ArrayDeclaration(null, new Identifier("*", getTokenLine(position - 1)),
                                new ArrayList<>(), getTokenLine(position - 1));
                        child = modifyType(declarator, decl);
                    } else {
                        child = new ArrayDeclaration(new TypeDeclaration(null, null, null),
                                new Identifier("*", getTokenLine(position -1 )), new ArrayList<>(), getTokenLine(position - 1));
                    }
                    child2 = rest22(child);
                    if (child2 == null) {
                        return null;
                    }
                    if (!child2.isEmpty()) {
                        return child2;
                    } else {
                        return child;
                    }
                } else {
                    position = pos;
                    symbolTable = copySymbolTable;
                    errorDatabase = copyErrorDatabase;
                    break;
                }
            case Tag.STATIC:
                nextToken();
                child1 = left27(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        child1 = assignment_expression();
        Node child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                if (declarator != null) {
                    decl = new ArrayDeclaration(null, child1, new ArrayList<>(), getTokenLine(position - 1));
                    child = modifyType(declarator, decl);
                } else {
                    child = new ArrayDeclaration(new TypeDeclaration(null, null, null), child1,
                            new ArrayList<>(), getTokenLine(position - 1));
                }
                child3 = rest22(child);
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child;
                }
            }
        }
        ArrayList<String> child4 = type_qualifier_list();
        if (!child4.isEmpty()) {
            child2 = left28(child4, declarator);
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return child2;
            }
        }
        return new None();
    }

    /**
     * left27 ->  type_qualifier_list assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left27(Node declarator) {
        ArrayList<String> child1 = type_qualifier_list();
        Node child2, child3, child4, decl, child;
        if (!child1.isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                child3 = expect(Tag.RIGHT_BRACKETS);
                if (child3 == null) {
                    return null;
                } else {
                    //pridanie satic na ziačiatok qualifiers
                    child1.add(0, "static");

                    if (declarator != null) {
                        decl = new ArrayDeclaration(null, child2, child1, getTokenLine(position - 1));
                        child = modifyType(declarator, decl);
                    } else {
                        child = new ArrayDeclaration(new TypeDeclaration(null, null, null), child2,
                                child1, getTokenLine(position - 1));
                    }
                    child4 = rest22(child);
                    if (child4 == null) {
                        return null;
                    }
                    if (!child4.isEmpty()) {
                        return child4;
                    } else {
                        return child;
                    }
                }
            }
        }
        child2 = assignment_expression();
        if (child2 == null) {
            return null;
        }
        if (!child2.isNone()) {
            child3 = expect(Tag.RIGHT_BRACKETS);
            if (child3 == null) {
                return null;
            } else {
                child1 = new ArrayList<>();
                child1.add("static");

                if (declarator != null) {
                    decl = new ArrayDeclaration(null, child2, child1, getTokenLine(position - 1));
                    child = modifyType(declarator, decl);
                } else {
                    child = new ArrayDeclaration(new TypeDeclaration(null, null, null), child2,
                            child1, getTokenLine(position - 1));
                }
                child4 = rest22(child);
                if (child4 == null) {
                    return null;
                }
                if (!child4.isEmpty()) {
                    return child4;
                } else {
                    return child;
                }
            }
        }
        return new None();
    }

    /**
     * left28 ->  STATIC assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     *          | ']' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left28(ArrayList<String> qualifiers, Node declarator) {
        Node child1, child2 = null, child3 = null, decl, child = null;
        switch (getTokenTag()) {
            case Tag.STATIC:
                nextToken();
                child1 = assignment_expression();
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    qualifiers.add("static");

                    if (declarator != null) {
                        decl = new ArrayDeclaration(null, child1, qualifiers, getTokenLine(position - 1));
                        child = modifyType(declarator, decl);
                    } else {
                        child = new ArrayDeclaration(new TypeDeclaration(null, null, null), child1,
                                qualifiers, getTokenLine(position - 1));
                    }
                    child3 = rest22(child);
                }
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child;
                }
            case Tag.RIGHT_BRACKETS:
                nextToken();
                decl = new ArrayDeclaration(new TypeDeclaration(null, null, null), null, qualifiers, getTokenLine(position - 1));
                if (declarator != null) {
                    child = modifyType(declarator, decl);
                } else {
                    child = decl;
                }
                child1 = rest22(child);
                if (child1 == null) {
                    return null;
                }
                if (!child1.isEmpty()) {
                    return child1;
                } else {
                    return child;
                }
        }
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                decl = new ArrayDeclaration(new TypeDeclaration(null, null, null), child1, qualifiers,
                        getTokenLine(position - 1));
                if (declarator != null) {
                    child = modifyType(declarator, decl);
                } else {
                    child = decl;
                }
                child3 = rest22(child);
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child;
                }
            }
        }
        return new None();
    }

    /**
     * rest22 ->  '[' left26
     *          | '(' left29
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node rest22(Node declarator) {
        Node child1;
        switch (getTokenTag()) {
            case Tag.RIGHT_BRACKETS:
                nextToken();
                child1 = left26(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = left29(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            default:
                return new EmptyStatement();
        }
    }

    /**
     * left29 ->  ')' rest22
     *          | parameter_type_list ')' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left29(Node declarator) {
        Node child1, decl, child;
        if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
            nextToken();
            decl = new FunctionDeclaration(null, null, getTokenLine(position - 1));
            child = modifyType(declarator, decl);
            child1 = rest22(child);
            if (child1 == null) {
                return null;
            }
            if (!child1.isEmpty()){
                return child1;
            } else {
                return child;
            }
        }
        child1 = parameter_type_list();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                decl = new FunctionDeclaration(child1, null, getTokenLine(position - 1));
                child = modifyType(declarator, decl);
                child3 = rest22(child);
                if (child3 == null) {
                    return null;
                }
                if (!child3.isEmpty()) {
                    return child3;
                } else {
                    return child;
                }
            }
        }
        return new None();
    }

    /**
     * initializer ->  '{' initializer_list '}'
     *               | '{' initializer_list ',' '}'
     *               | assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node initializer() {
        Node child1, child2;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            nextToken();
            child1 = initializer_list();
            if (child1 != null && !child1.isNone()) {
                switch (getTokenTag()) {
                    case Tag.RIGHT_BRACES:
                        nextToken();
                        return child1;
                    case Tag.COMMA:
                        nextToken();
                        child2 = expect(Tag.RIGHT_BRACES);
                        if (child2 != null) {
                            return child1;
                        }
                }
            }
            //error recovery
            while (getTokenTag() != Tag.RIGHT_BRACES) {
                if (position == tokenStream.size() - 1) {
                    return null;
                }
                nextToken();
            }
            nextToken();
            return new Err();
        }
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * initializer_list ->  designation initializer rest23
     *                    | initializer rest23
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node initializer_list() {
        ArrayList<Node> child1 = designation();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.isEmpty()) {
            child2 = initializer();
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                ArrayList<Node> arr = new ArrayList<>();
                arr.add(new NamedInitializer(child1, child2));
                InitializationList init = new InitializationList(arr, child2.getLine());
                child3 = rest23(init);
                if (child3 == null) {
                    return null;
                } else {
                    return init;
                }
            }
        }
        child2 = initializer();
        if (child2 == null) {
            return null;
        }
        if (!child2.isNone()) {
            ArrayList<Node> arr = new ArrayList<>();
            arr.add(child2);
            InitializationList init = new InitializationList(arr, child2.getLine());
            child3 = rest23(init);
            if (child3 == null) {
                return null;
            } else {
                return init;
            }
        }
        return new None();
    }

    /**
     * rest23 ->  ',' designation initializer rest23
     *          | ',' initializer rest23
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node rest23(InitializationList init) {
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            ArrayList<Node> child1 = designation();
            Node child2, child3;
            if (child1 == null) {
                return null;
            }
            if (!child1.isEmpty()) {
                child2 = initializer();
                if (child2 == null) {
                    return null;
                }
                if (child2.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                } else {
                    init.addExpression(new NamedInitializer(child1, child2));
                    child3 = rest23(init);
                    if (child3 == null) {
                        return null;
                    } else {
                        return new EmptyStatement();
                    }
                }
            }
            child2 = initializer();
            if (child2 == null) {
                return null;
            }
            if (!child2.isNone()) {
                init.addExpression(child2);
                child3 = rest23(init);
                if (child3 == null) {
                    return null;
                } else {
                    return new EmptyStatement();
                }
            }
            System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
            errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
            return null;
        }
        return new EmptyStatement();
    }

    /**
     * designation -> designator_list '='
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> designation() {
        ArrayList<Node> child1 = designator_list();
        if (child1 == null) {
            return null;
        }
        if (!child1.isEmpty()) {
            if (getTokenValue().equals("=")) {
                nextToken();
                return child1;
            } else {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            }
        }
        return new ArrayList<>();
    }

    /**
     * designator_list ->  designator_list designator
     *                   | designator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> designator_list() {
        Node child1 = designator();
        if (child1 == null) {
            return null;
        }
        ArrayList<Node> arr = new ArrayList<>();
        if (!child1.isNone()) {
            arr.add(child1);
            child1 = designator();
            if (child1 == null) {
                return null;
            }
            while (!child1.isNone()) {
                arr.add(child1);
                child1 = designator();
                if (child1 == null) {
                    return null;
                }
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * designator ->  '[' constant_expression ']'
     *              | '.' IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node designator() {
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = constant_expression();
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.DOT:
                nextToken();
                child1 = expect(Tag.IDENTIFIER);
                if (child1 != null) {
                    return new Identifier(getTokenValue(position - 1), getTokenLine(position - 1));
                }
                return null;
        }
        return new None();
    }

    /**
     * statement ->  labeled_statement
     *             | compound_statement
     *             | expression_statement
     *             | selection_statement
     *             | iteration_statement
     *             | jump_statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node statement(boolean createSymbolTable) {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1 = labeled_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        position = pos;
        symbolTable = copySymbolTable;
        errorDatabase = copyErrorDatabase;
        child1 = compound_statement(createSymbolTable);
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        child1 = expression_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        child1 = selection_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        child1 = iteration_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        child1 = jump_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * labeled_statement ->  IDENTIFIER ':' statement
     *                     | CASE constant_expression ':' statement
     *                     | DEFAULT ':' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node labeled_statement() {
        SymbolTable copySymbolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySymbolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1, child2 = null, child3 = null;
        int line;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                String terminal = getTokenValue();
                line = getTokenLine();
                nextToken();
                child1 = accept(Tag.COLON);
                if (child1 != null) {
                    child2 = statement(true);
                }
                if (child2 != null && !child2.isNone()) {
                    return new Label(terminal, child2, line);
                }
                position = pos;
                symbolTable = copySymbolTable;
                errorDatabase = copyErrorDatabase;
                break;
            case Tag.DEFAULT:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.COLON);
                if (child1 != null) {
                    child2 = statement(true);
                }
                if (child2 != null && !child2.isNone()) {
                    return new Default(child2, line);
                }
                if (child2 != null && child2.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.CASE:
                line = getTokenLine();
                nextToken();
                child1 = constant_expression();
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.COLON);
                }
                if (child2 != null) {
                    child3 = statement(true);
                }
                if (child3 != null && !child3.isNone()) {
                    return new Case(child1, child3, line);
                }
                if ((child1 != null && child1.isNone()) || (child3 != null && child3.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * compound_statement ->  '{' '}'
     *                      | '{' block_item_list '}'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node compound_statement(boolean createSymbolTable) {
        if (getTokenTag() == Tag.LEFT_BRACES) {
            int line = getTokenLine();
            nextToken();
            if (getTokenTag() == Tag.RIGHT_BRACES) {
                nextToken();
                return new Compound(null, line);
            }

            SymbolTable parent = symbolTable;
            if (createSymbolTable) {
                //vytvorenie vnorenej tabuľky
                symbolTable = new SymbolTable(symbolTable);
                if (parent != null) {
                    parent.addChild(symbolTable);
                }
            }

            ArrayList<Node> child1 = block_item_list();
            Node child2;
            if (child1 == null) {
                //error recovery
                while (getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                    //ak je koniec súboru
                    if (position == tokenStream.size() - 1) {
                        return null;
                    }
                }
                nextToken();
                return new Err();
            }
            if (!child1.isEmpty()) {
                child2 = expect(Tag.RIGHT_BRACES);
                if (child2 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    nextToken();
                    return new Err();
                } else {
                    if (createSymbolTable) {
                        symbolTable = parent;
                    }
                    return new Compound(child1, line);
                }
            }
        }
        return new None();
    }

    /**
     * block_item_list ->  block_item_list block_item
     *                   | block_item
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> block_item_list() {
        ArrayList<Node> arr = new ArrayList<>();
        ArrayList<Node> child1 = block_item();
        if (child1 == null) {
            return null;
        }
        if (!child1.isEmpty()) {
            arr.addAll(child1);
            child1 = block_item();
            if (child1 == null) {
                return null;
            }
            while (!child1.isEmpty()) {
                arr.addAll(child1);
                child1 = block_item();
                if (child1 == null) {
                    return null;
                }
            }
            return arr;
        }
        return arr;
    }

    /**
     * block_item ->  declaration
     *              | statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> block_item() {
        ArrayList<Node> child1 = declaration();
        if (child1 == null) {
            return null;
        }
        if (!child1.isEmpty()) {
            return child1;
        }
        Node child2 = statement(true);
        if (child2 == null) {
            return null;
        }
        if (!child2.isNone()) {
            ArrayList<Node> arr = new ArrayList<>();
            arr.add(child2);
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * expression_statement ->  ';'
     *                        | expression ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node expression_statement() {
        if (getTokenTag() == Tag.SEMICOLON) {
            nextToken();
            return new EmptyStatement();
        }
        Node child1 = expression();
        Node child2;
        if (child1 == null) {
            //error recovery
            while (getTokenTag() != Tag.SEMICOLON) {
                nextToken();
                //ak je koniec súboru
                if (position == tokenStream.size() - 1) {
                    return null;
                }
            }
            nextToken();
            return new Err();
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.SEMICOLON);
            if (child2 == null) {
                //error recovery
                while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                    //ak je koniec súboru
                    if (position == tokenStream.size() - 1) {
                        return null;
                    }
                }
                if (getTokenTag() == Tag.SEMICOLON) {
                    nextToken();
                    return new Err();
                } else {
                    return null;
                }
            } else {
                return child1;
            }
        }
        return new None();
    }

    /**
     * selection_statement ->  IF '(' expression ')' statement ELSE statement
     *                       | IF '(' expression ')' statement
     *                       | SWITCH '(' expression ')' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node selection_statement() {
        Node child1, child2 = null, child3 = null, child4 = null, child5 = null;
        int line;
        switch (getTokenTag()) {
            case Tag.IF:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.LEFT_PARENTHESES);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.isNone()) {
                    child3 = expect(Tag. RIGHT_PARENTHESES);
                }
                if (child3 != null) {
                    child4 = statement(true);
                }
                if (child4 != null && !child4.isNone()) {
                    if (getTokenTag() == Tag.ELSE) {
                        nextToken();
                        child5 = statement(true);
                        if (child5 != null && !child5.isNone()) {
                            return new If(child2, child4, child5, line);
                        }
                    } else {
                        return new If(child2, child4, null, line);
                    }
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())
                        || (child5 != null && child5.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.SWITCH:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.LEFT_PARENTHESES);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.isNone()) {
                    child3 = expect(Tag. RIGHT_PARENTHESES);
                }
                if (child3 != null) {
                    child4 = statement(true);
                }
                if (child4 != null && !child4.isNone()) {
                    return new Switch(child2, child4, line);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * iteration_statement ->  WHILE '(' expression ')' statement
     *                       | DO statement WHILE '(' expression ')' ';'
     *                       | FOR '(' left33
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node iteration_statement() {
        Node child1, child2 = null, child3 = null, child4 = null;
        Node child5 = null, child6 = null;
        int line;
        switch (getTokenTag()) {
            case Tag.WHILE:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag. LEFT_PARENTHESES);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.isNone()) {
                    child3 = expect(Tag. RIGHT_PARENTHESES);
                }
                if (child3 != null) {
                    child4 = statement(true);
                }
                if (child4 != null && !child4.isNone()) {
                    return new While(child2, child4, line);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.DO:
                line = getTokenLine();
                nextToken();
                child1 = statement(true);
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.WHILE);
                }
                if (child2 != null) {
                    child3 = expect(Tag.LEFT_PARENTHESES);
                }
                if (child3 != null) {
                    child4 = expression();
                }
                if (child4 != null && !child4.isNone()) {
                    child5 = expect(Tag.RIGHT_PARENTHESES);
                }
                if (child5 != null) {
                    child6 = expect(Tag.SEMICOLON);
                }
                if (child1 == null || child2 == null || child3 == null || child4 == null || child5 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    nextToken();
                    return new Err();
                }
                if (child6 != null) {
                    return new DoWhile(child4, child1, line);
                } else {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    if (getTokenTag() == Tag.SEMICOLON) {
                        nextToken();
                        return new Err();
                    } else {
                        return null;
                    }
                }
            case Tag.FOR:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.LEFT_PARENTHESES);
                if (child1 != null) {
                    child2 = left33(line);
                }
                if (child2 == null) {
                    return null;
                }
                if (!child2.isNone()) {
                    return child2;
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * left33 ->  expression_statement expression_statement ')' statement
     *          | expression_statement expression_statement expression ')' statement
     *          | declaration expression_statement ')' statement
     *          | declaration expression_statement expression ')' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node left33(int line) {
        SymbolTable parent = symbolTable;
        //vytvorenie vnorenej tabuľky
        symbolTable = new SymbolTable(symbolTable);
        if (parent != null) {
            parent.addChild(symbolTable);
        }

        Node child1 = expression_statement();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            child2 = expression_statement();
            if (child2 == null) {
                return null;
            }
             if (child2.isNone()) {
                 System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                 errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                 if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
                     nextToken();
                     child3 = statement(false);
                     symbolTable = parent;
                     if (child3 != null && !child3.isNone()) {
                         if (child1.isEmpty()) {
                             child1 = null;
                         }
                         if (child2.isEmpty()) {
                             child2 = null;
                         }
                         return new For(child1, child2, null, child3, line);
                     }
                     if (child3 != null && child3.isNone()) {
                         System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                         errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                     }
                     return null;
                 }
                 child3 = expression();
                 Node child4, child5;
                 if (child3 == null) {
                     return null;
                 }
                 if (!child3.isNone()) {
                     child4 = expect(Tag.RIGHT_PARENTHESES);
                     if (child4 == null) {
                         return null;
                     } else {
                         child5 = statement(false);
                         symbolTable = parent;
                         if (child5 == null) {
                             return null;
                         }
                         if (child5.isNone()) {
                             System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                             errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                             return null;
                         } else {
                             if (child1.isEmpty()) {
                                 child1 = null;
                             }
                             if (child2.isEmpty()) {
                                 child2 = null;
                             }
                             return new For(child1, child2, child3, child5, line);
                         }
                     }
                 } else {
                     System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                     errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                     return null;
                 }
            }
        }
        ArrayList<Node> child = declaration();
        if (child == null) {
            return null;
        }
        if (!child.isEmpty()) {
            child2 = expression_statement();
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
                    nextToken();
                    child3 = statement(false);
                    symbolTable = parent;
                    if (child3 != null && !child3.isNone()) {
                        if (child2.isEmpty()) {
                            child2 = null;
                        }
                        return new For(new DeclarationList(child, line), child2, null, child3, line);
                    }
                    if (child3 != null && child3.isNone()) {
                        System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                        errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    }
                    return null;
                }
                child3 = expression();
                Node child4, child5;
                if (child3 == null) {
                    return null;
                }
                if (!child3.isNone()) {
                    child4 = expect(Tag.RIGHT_PARENTHESES);
                    if (child4 == null) {
                        return null;
                    } else {
                        child5 = statement(false);
                        symbolTable = parent;
                        if (child5 == null) {
                            return null;
                        }
                        if (child5.isNone()) {
                            System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                            errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                            return null;
                        } else {
                            if (child2.isEmpty()) {
                                child2 = null;
                            }
                            return new For(new DeclarationList(child, line), child2, child3, child5, line);
                        }
                    }
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    errorDatabase.addErrorMessage(getTokenLine(), err.getError("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
        }
        return new None();
    }

    /**
     * jump_statement ->  GOTO IDENTIFIER ';'
     *                  | CONTINUE ';'
     *                  | BREAK ';'
     *                  | RETURN ';'
     *                  | RETURN expression ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node jump_statement() {
        Node child1, child2;
        int line;
        switch (getTokenTag()) {
            case Tag.GOTO:
                line = getTokenLine();
                String terminal;
                nextToken();
                child1 = expect(Tag.IDENTIFIER);
                if (child1 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    nextToken();
                    return new Err();
                } else {
                    terminal = getTokenValue(position - 1);
                    child2 = expect(Tag.SEMICOLON);
                }
                if (child2 != null) {
                    return new Goto(terminal, line);
                } else {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    if (getTokenTag() == Tag.SEMICOLON) {
                        nextToken();
                        return new Err();
                    } else {
                        return null;
                    }
                }
            case Tag.CONTINUE:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.SEMICOLON);
                if (child1 != null) {
                    return new Continue(line);
                } else {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    if (getTokenTag() == Tag.SEMICOLON) {
                        nextToken();
                        return new Err();
                    } else {
                        return null;
                    }
                }
            case Tag.BREAK:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.SEMICOLON);
                if (child1 != null) {
                    return new Break(line);
                } else {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    if (getTokenTag() == Tag.SEMICOLON) {
                        nextToken();
                        return new Err();
                    } else {
                        return null;
                    }
                }
            case Tag.RETURN:
                line = getTokenLine();
                nextToken();
                if (getTokenTag() == Tag.SEMICOLON) {
                    nextToken();
                    return new Return(null, line);
                }
                child1 = expression();
                if (child1 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON) {
                        nextToken();
                        //ak je koniec súboru
                        if (position == tokenStream.size() - 1) {
                            return null;
                        }
                    }
                    nextToken();
                    return new Err();
                }
                if (!child1.isNone()) {
                    child2 = expect(Tag.SEMICOLON);
                    if (child2 == null) {
                        //error recovery
                        while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                            nextToken();
                            //ak je koniec súboru
                            if (position == tokenStream.size() - 1) {
                                return null;
                            }
                        }
                        if (getTokenTag() == Tag.SEMICOLON) {
                            nextToken();
                            return new Err();
                        } else {
                            return null;
                        }
                    } else {
                        return new Return(child1, line);
                    }
                }
                //TODO: odchytiť chybu??
                return null;
        }
        return new None();
    }

    /**
     * translation_unit ->  translation_unit external_declaration
     *                    | external_declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> translation_unit() {
        ArrayList<Node> child1 = external_declaration();
        if (child1 != null && !child1.isEmpty()) {
            ArrayList<Node> arr = new ArrayList<>(child1);
            child1 = external_declaration();
            if (child1 == null) {
                return null;
            }
            while (!child1.isEmpty()) {
                arr.addAll(child1);
                child1 = external_declaration();
                if (child1 == null) {
                    return null;
                }
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * external_declaration ->  function_definition
     *                        | declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private ArrayList<Node> external_declaration() {
        SymbolTable copySmybolTable = null;
        ErrorDatabase copyErrorDatabase = null;
        try {
            copySmybolTable = (SymbolTable) symbolTable.clone();
            copyErrorDatabase = (ErrorDatabase) errorDatabase.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int pos = position;
        Node child1 = function_definition();
        if (!child1.isNone()) {
            ArrayList<Node> arr = new ArrayList<>();
            arr.add(child1);
            return arr;
        }
        position = pos;
        symbolTable = copySmybolTable;
        errorDatabase = copyErrorDatabase;
        ArrayList<Node> child2 = declaration();
        if (child2 == null) {
            return null;
        }
        if (!child2.isEmpty()) {
            return child2;
        }
        return new ArrayList<>();
    }

    /**
     * function_definition ->  declaration_specifiers declarator declaration_list compound_statement
     *                       | declaration_specifiers declarator compound_statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private Node function_definition() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = declaration_specifiers(typeNode);
        Node child2 = null, child3;
        if (child1 != null && !child1.isNone()) {
            child2 = declarator();
        }
        if (child2 != null && !child2.isNone()) {
            typeNode = (TypeNode) child1;
            ArrayList<Node> child = declaration_list();
            Node child4 = null;
            if (child != null && !child.isEmpty()) {
                child4 = compound_statement(true);
            }
            if (child4 != null && !child4.isNone()) {
                return createFunction(typeNode, child2, child, child4);
            }
            child3 = compound_statement(true);
            if (child3 != null && !child3.isNone()) {
                return createFunction(typeNode, child2, null, child3);
            }
        }
        return new None();
    }

    /**
     * declaration_list ->  declaration_list declaration
     *                    | declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private ArrayList<Node> declaration_list() {
        ArrayList<Node> child1 = declaration();
        if (child1 == null) {
            return null;
        }
        ArrayList<Node> arr = new ArrayList<>(child1);
        if (!child1.isEmpty()) {
            arr.addAll(child1);
            child1 = declaration();
            if (child1 == null) {
                return null;
            }
            while (!child1.isEmpty()) {
                arr.addAll(child1);
                child1 = declaration();
                if (child1 == null) {
                    return null;
                }
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     *
     * @param typeNode
     * @param declarations
     * @return
     */
    private ArrayList<Node> createDeclaration(TypeNode typeNode, ArrayList<Node> declarations) {
        return createDeclaration(typeNode, declarations, false, false);
    }

    /**
     *
     * @param typeNode
     * @param declarations
     * @param structVariable
     * @return
     */
    private ArrayList<Node> createDeclaration(TypeNode typeNode, ArrayList<Node> declarations, boolean structVariable) {
        return createDeclaration(typeNode, declarations, false, structVariable);
    }

    /**
     *
     * @param typeNode
     * @param declarations
     * @param parameter
     * @return
     */
    private ArrayList<Node> createDeclaration(TypeNode typeNode, ArrayList<Node> declarations, boolean parameter,
                                              boolean structVariable) {
        ArrayList<Node> decls = new ArrayList<>();

        boolean typedef = typeNode.getStorage().contains("typedef");

        Declarator declarator = (Declarator) declarations.get(0);

        if (declarator.getDeclarator() == null) {
            declarator.addDeclarator(new TypeDeclaration(((IdentifierType) typeNode.getLastType()).getName(0),
                    null, null));
            typeNode.removeLastType();
        } else if (!declarator.getDeclarator().isEnumStructUnion() && !declarator.isIdentifierType()) {
            Node tail = declarator.getDeclarator();

            while(!tail.isTypeDeclaration()) {
                tail = tail.getType();
            }

            if (((TypeDeclaration) tail).getDeclname() == null) {
                String name = ((IdentifierType) typeNode.getLastType()).getName(0);
                ((TypeDeclaration) tail).addDeclname(name);
                typeNode.removeLastType();
            }

            Node declaration;
            for (Node decl : declarations) {
                Declarator declarator1 = (Declarator) decl;
                if (declarator1.getDeclarator() == null) return null;
                if (typedef) {
                    declaration = new Typedef(null, typeNode.getQualifiers(), typeNode.getStorage(),
                            declarator1.getDeclarator(), declarator1.getDeclarator().getLine());
                } else {

                    //TODO: pozrieť sa na bitsize
                    declaration = new Declaration(null, typeNode.getQualifiers(), typeNode.getStorage(),
                            declarator1.getDeclarator(), declarator1.getInitializer(),null, declarator1.getDeclarator().getLine());
                }
                if (!declaration.getType().isEnumStructUnion() && !declaration.getType().isIdentifierType()) {
                    declaration = fixTypes(declaration, typeNode.getTypes());
                }

                if (!typeChecking(((Declarator) decl).getDeclarator(),((Declarator) decl).getInitializer())) {
                    //TODO: Sémantická chyba + zistiť riadok problém ak je TypeDeclaration
                    System.out.println("Sémantická chyba na riadku " + declarator1.getInitializer().getLine() + "!");
                    //return null
                }

                addRecordToSymbolTable(declarator1, typedef, parameter, structVariable);

                decls.add(declaration);
            }
        }

        return decls;
    }

    /**
     *
     * @param declaration
     * @param typename
     * @return
     */
    private Node fixTypes(Node declaration, ArrayList<Node> typename) {
        DeclarationNode decl = (DeclarationNode) declaration;

        Node type = declaration;
        while (!type.isTypeDeclaration()) {
            type = type.getType();
        }

        assert type instanceof TypeDeclaration;
        decl.addName(((TypeDeclaration) type).getDeclname());
        ((TypeDeclaration) type).addQualifiers(decl.getQualifiers());

        //Riešenie chýb
        for (Node t_name : typename) {
            if (!t_name.isIdentifierType()) {
                if (typename.size() > 1) {
                    System.out.println("Chybný typ!");
                    errorDatabase.addErrorMessage(111, err.getError("E-SmA-01"), "E-SmA-01");
                    return null;
                } else {
                    type.addType(t_name);
                    return decl;
                }
            }
        }

        if (typename.isEmpty()) {
            if (!(decl.getType() instanceof FunctionDeclaration)) {
                System.out.println("Chýbajúci typ!");
                errorDatabase.addErrorMessage(111, err.getError("E-SmA-01"), "E-SmA-01");
                return null;
            }
            ArrayList<String> arr = new ArrayList<>();
            arr.add("int");
            type.addType(new IdentifierType(arr, decl.getLine()));
        } else {
            ArrayList<String> arr = new ArrayList<>();
            for (Node t_name : typename) {
                arr.addAll(((IdentifierType) t_name).getNames());
            }

            type.addType(new IdentifierType(arr, typename.get(0).getLine()));
        }

        return decl;
    }

    /**
     *
     * @param declaration
     * @param modifier
     * @return
     */
    private Node modifyType(Node declaration, Node modifier) {
        Node tail = modifier;

        while (tail.getType() != null) {
            tail = tail.getType();
        }

        if (declaration.isTypeDeclaration()) {
            tail.addType(declaration);
            return modifier;
        } else {
            Node declaration_tail = declaration;

            while (!declaration_tail.getType().isTypeDeclaration()) {
                declaration_tail = declaration_tail.getType();
            }

            tail.addType(declaration_tail.getType());
            declaration_tail.addType(modifier);

            return declaration;
        }
    }

    /**
     *
     * @param declarator1
     * @param typedef
     * @param parameter
     * @param structVariable
     */
    private void addRecordToSymbolTable(Declarator declarator1, boolean typedef, boolean parameter,
                                        boolean structVariable) {

        if (typedef) {
            if (declarator1.getDeclarator() instanceof PointerDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                int line = 0;
                String name = "";
                String type = "";
                String struct_name = "";
                byte typeCategory;

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
                    typeCategory = findType(type);
                } else {
                    typeCategory = findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                symbolTable.insert(name, typeCategory, type, line, Kind.TYPEDEF_NAME, errorDatabase);
                System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.TYPEDEF_NAME");

            } else if (declarator1.getDeclarator() instanceof TypeDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                String name = ((TypeDeclaration) declarator1.getDeclarator()).getDeclname();
                int line = 0;
                String type = "";
                byte typeCategory;
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
                    typeCategory = findType(type);
                } else {
                    typeCategory = findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                symbolTable.insert(name, typeCategory, type, line, Kind.TYPEDEF_NAME, errorDatabase);
                System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.TYPEDEF_NAME");
            }
        } else {
            if (declarator1.getDeclarator() instanceof PointerDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                int line = 0;
                String name = "";
                String type = "";
                String struct_name = "";
                byte typeCategory;

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
                    typeCategory = findType(type);
                } else {
                    typeCategory = findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                if (structVariable) {
                    symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_PARAMETER, errorDatabase);
                    System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_PARAMETER");
                } else {
                    if (parameter) {
                        symbolTable.insert(name, typeCategory, type, line, Kind.PARAMETER, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.PARAMETER");
                    } else {
                        symbolTable.insert(name, typeCategory, type, line, Kind.VARIABLE, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.VARIABLE");
                    }
                }

            } else if (declarator1.getDeclarator() instanceof TypeDeclaration) {
                Node decl_tail = declarator1.getDeclarator().getType();
                String name = ((TypeDeclaration) declarator1.getDeclarator()).getDeclname();
                int line = 0;
                String type = "";
                String struct_name = "";
                byte typeCategory;

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
                    typeCategory = findType(type);
                } else {
                    typeCategory = findType(type);
                    type = type.replaceFirst(" ", " " + struct_name + " ");
                }

                if (structVariable) {
                    symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_PARAMETER, errorDatabase);
                    System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_PARAMETER");
                } else {
                    if (parameter) {
                        symbolTable.insert(name, typeCategory, type, line, Kind.PARAMETER, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.PARAMETER");
                    } else {
                        symbolTable.insert(name, typeCategory, type, line, Kind.VARIABLE, errorDatabase);
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
                byte typeCategory;

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
                        typeCategory = findType(type);
                    } else {
                        type += "* ";
                        typeCategory = findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                } else {
                    if (type.equals("")) {
                        type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                        line = decl_tail.getLine();
                        typeCategory = findType(type);
                    } else {
                        typeCategory = findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                }

                if (size < 0) {
                    if (structVariable) {
                        symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_ARRAY_PARAMETER, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_ARRAY_PARAMETER");
                    } else {
                        if (parameter) {
                            symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY_PARAMETER, errorDatabase);
                            System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.ARRAY_PARAMETER");
                        } else {
                            symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY, errorDatabase);
                            System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.ARRAY");
                        }
                    }
                } else {
                    if (structVariable) {
                        symbolTable.insert(name, typeCategory, type, line, Kind.STRUCT_ARRAY_PARAMETER, size, errorDatabase);
                        System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.STRUCT_ARRAY_PARAMETER, " + size);
                    } else {
                        if (parameter) {
                            symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY_PARAMETER, size, errorDatabase);
                            System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.ARRAY_PARAMETER, " + size);
                        } else {
                            symbolTable.insert(name, typeCategory, type, line, Kind.ARRAY, size, errorDatabase);
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
                byte typeCategory;

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
                        typeCategory = findType(type);
                    } else {
                        type += "* ";
                        typeCategory = findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                } else {
                    if (type.equals("")) {
                        type = String.join(" ", ((IdentifierType) decl_tail).getNames()) + " ";
                        line = decl_tail.getLine();
                        typeCategory = findType(type);
                    } else {
                        typeCategory = findType(type);
                        type = type.replaceFirst(" ", " " + struct_name + " ");
                    }
                }

                symbolTable.insert(name, typeCategory, type, line, Kind.FUNCTION, errorDatabase);
                System.out.println("Insert: " + name + ", " + type + ", " + line + ", Kind.FUNCTION");

                Record record = symbolTable.lookup(name);

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
                        symbolTable.setValue(name, record);
                        System.out.println("Update: " + name);
                    }
                }
            }
        }
    }

    private Node createFunction(TypeNode typeNode, Node declaration, ArrayList<Node> parameters, Node body) {

        ArrayList<Node> arr = new ArrayList<>();
        arr.add(new Declarator(declaration, null));
        Node decl = createDeclaration(typeNode, arr).get(0);

        return new FunctionDefinition(decl, parameters, body, decl.getLine());
    }

    private boolean typeChecking(Node declarator, Node initializer) {
        if (initializer == null || declarator == null) {
            return true;
        }

        if (declarator instanceof TypeDeclaration) {
            Node tail = declarator.getType();
            String type = "";

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
                tail = tail.getType();
            }

            if (type.equals("")) {
                type = String.join(" ", ((IdentifierType) tail).getNames()) + " ";
            }
            //tail je IdentifierType
            byte type1 = findType(type);
            byte type2 = getInitializer(initializer);

            if (type2 == -2) {
                return true;
            }

            if (type1 == -1 || type2 < 0) {
                return false;
            }

            if (type1 == Type.BOOL || type2 == Type.BOOL ||
                    type1 == Type.VOID || type2 == Type.VOID) {
                return false;
            }

            if (type1 == type2) {
                return true;
            }

            if (type1 > 28 || type2 > 28) {
                return false;
            }

            //pozrieť sa na typy ak väčší typ dávam do menšieho, ale aj pointre
            return true;
        } else if (declarator instanceof ArrayDeclaration) {
            Node tail = declarator.getType();
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

            byte type1 = findType(type);
            byte type2 = getInitializer(initializer);

            if (type2 == -2) {
                return true;
            }

            if((type1 == Type.CHAR || type1 == Type.UNSIGNEDCHAR || type1 == Type.SIGNEDCHAR) && type2 == Type.STRING) {
                return true;
            }
            if (type2 == -1) {
                return true;
            }
            if (type1 == type2) {
                return true;
            }
            return false;
        } else if (declarator instanceof PointerDeclaration) {
            Node tail = declarator.getType();
            String type = "";

            while (!(tail instanceof IdentifierType)) {
                if (tail.isEnumStructUnion()) {
                    if (tail instanceof Enum) {
                        type = "enum * ";
                    } else if (tail instanceof Struct) {
                        type = "struct * ";
                    } else {
                        type = "union * ";
                    }
                    break;
                }
                tail = tail.getType();
            }

            if (type.equals("")) {
                type = String.join(" ", ((IdentifierType) tail).getNames()) + " * ";
            }

            byte type1 = findType(type);
            byte type2 = getInitializer(initializer);

            if (type2 == -2) {
                return true;
            }

            if (type2 < 0 || type1 == -1) {
                return false;
            }
            if (type1 == type2) {
                return true;
            }

            if (type2 < 50) {
                //type2 nemá typ pointra;
                return false;
            }
            if (type1 > 78 || type2 > 78) {
                return false;
            }
            //TODO: neviem
            return true;
        } else {
            return false;
        }

    }

    private byte getInitializer(Node initializer) {
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
            return findType(type);
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
            return findType(((Constant) initializer).getTypeSpecifier() + " ");
        } else if (initializer instanceof FunctionCall) {
            Identifier id = (Identifier) ((FunctionCall) initializer).getName();
            Record record = symbolTable.lookup(id.getName());
            if (record == null) {
                return -2;                                                                  //vracia -2 ako informáciu, že nenašiel záznam v symbolicek tabuľke
            } else {
                return record.getType();
            }
        } else if (initializer instanceof ArrayReference) {
            Identifier id = (Identifier) ((ArrayReference) initializer).getName();
            Record record = symbolTable.lookup(id.getName());
            if (record == null) {
                return -128;
            } else {
                return record.getType();
            }
        } else if (initializer instanceof  StructReference) {
            Identifier id = (Identifier) ((StructReference) initializer).getName();
            Record record = symbolTable.lookup(id.getName());
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
            case "size_t ": return (byte) (Type.UNSIGNEDINT + pointer);                                     // size_t
            case "FILE ": return (byte) (Type.FILE + pointer);                                              // FILE
            case "string ": return Type.STRING;
            default: return Type.TYPEDEF_TYPE;                                                              // vlastný typ
        }
    }


}