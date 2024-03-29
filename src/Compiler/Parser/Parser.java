package Compiler.Parser;

import Backend.InternationalizationClass;
import Compiler.AbstractSyntaxTree.Enum;
import Compiler.Errors.ErrorDatabase;
import Compiler.GraphColoring.VariableUsageChecker;
import Compiler.Lexer.Scanner;
import Compiler.Lexer.Token;
import Compiler.Lexer.Tag;
import Compiler.SymbolTable.*;
import java.util.ArrayList;
import java.util.Collections;
import Compiler.AbstractSyntaxTree.*;

/**
 * Trieda, ktorá spracováva tokeny a mení ich na abstraktný syntaktický strom.
 *
 * <p> Trieda predstavuje syntaktickú analýzu spolu so sémantickou analýzou.
 *
 * @author Ivan Vykopal
 */
public class Parser {
    /** Atribút position predstavuje pozíciu aktuálne spracovávaného tokenu. **/
    private int position = 0;

    /** Atribút tokenStream predstavuje zoznam tokenov pre spracovanie. **/
    public ArrayList<Token> tokenStream = new ArrayList<>();

    /** Atribút errorDatabse predstavuje databázu chýb. **/
    private ErrorDatabase errorDatabase;

    /**
     * Atribút lastStatementLine predstavuje riadok, na ktorom sa poslednýkrát vyskytol ""statement"
     *
     * <p> Slúži najmä pre prídávanie riadku využitia, prípadne aj inicializácie pri for a while cykle.
     **/
    private int lastStatementLine = -1;

    /** Atribút symbolTable predstavuje zreťazený zoznam symbolických tabuliek. **/
    public SymbolTable symbolTable = new SymbolTable(null);

    /**
     * Konštruktor, ktorý zadaný súbor v textovej podobe spracuje prostredníctvom triedy {@code Scanner} a premení vstup
     * na prúd tokenov. Tieto tokeny sú uložené v atribúte tokenStream.
     *
     * @param file analyzovaný súbor v textovej podobe
     *
     * @param errorDatabase databáza chýb
     */
    public Parser(String file, ErrorDatabase errorDatabase) {
        this.errorDatabase = errorDatabase;
        Scanner scanner = new Scanner(file, errorDatabase);
        Token tok;
        while (true) {
            tok = scanner.scan();
            if (tok.tag == Tag.EOF) {
                break;
            } else {
                //System.out.println(tok.line + ": " + tok.value + ", " + tok.tag);
                tokenStream.add(tok);
            }
        }
    }

    /**
     * Metóda pre spustenie syntaktickej a sémantickej analýzy pre zdrojový kód.
     *
     * <p> Súčasťou tejto metódy je aj nájdenie globálnych premenných, dlho aktívnych premenných a aj neptimálne
     * využívanie premenných v programe.
     *
     * @param fileName názov analyzovaného súboru
     */
    public void parse(String fileName) {
        ArrayList<Node> child = translation_unit();
        if (child == null) {
            System.out.println("Chyba v parse tree!");
        } else {
            Node parseTree = new AST(child);
            //parseTree.traverse("");
            //symbolTable.printSymbolTable(0);
            if (errorDatabase.isEmpty()) {
                symbolTable.findGlobalVariable(errorDatabase);
                symbolTable.findLongActiveVariable(errorDatabase);
                new VariableUsageChecker(symbolTable, errorDatabase, fileName);
            } else {
                symbolTable.findGlobalVariable(errorDatabase);
                symbolTable.findLongActiveVariable(errorDatabase);
            }
        }
    }

    /**
     * Metóda pre načítanie nasledujúceho tokenu.
     */
    private void nextToken() {
        if (position != tokenStream.size() - 1) {
            position++;
        }
    }

    /**
     * Metóda pre zistenie riadku aktuálne spracovávaného tokenu.
     *
     * @return riadok aktuálne spracovávaného tokenu
     */
    private int getTokenLine() {
        return tokenStream.get(position).line;
    }

    /**
     * Metóda pre zistenie riadku tokenu, ktorý sa nachádza na zvolenej pozícii.
     *
     * @param index pozícia tokenu
     *
     * @return riadok pre token na zvolenej pozícii
     */
    private int getTokenLine(int index) {
        return tokenStream.get(index).line;
    }

    /**
     * Metóda pre zistenie hodnoty aktuálne spracovávaného tokenu.
     *
     * @return hodnota aktuálne spracovávaného tokenu
     */
    private String getTokenValue() {
        return tokenStream.get(position).value;
    }

    /**
     * Metóda pre zistenie hodnoty tokenu, ktorý sa nachádza na zvolenej pozícii.
     *
     * @param index pozícia tokenu
     *
     * @return hodnota pre token na zvolenej pozícii
     */
    private String getTokenValue(int index) {
        return tokenStream.get(index).value;
    }

    /**
     * Metóda pre zistenie triedy aktuálne spracovávaného tokenu.
     *
     * @return trieda aktuálne spracovávaného tokenu
     */
    private byte getTokenTag() {
        return tokenStream.get(position).tag;
    }

    /**
     * Metóda pre zistenie triedy tokenu, ktorý sa nachádza na zvolenej pozícii.
     *
     * @param index pozícia tokenu
     *
     * @return trieaa pre token na zvolenej pozícii
     */
    private byte getTokenTag(int index) {
        return tokenStream.get(index).tag;
    }

    /**
     * Metóda pre zistenie, či aktuálny token je rovnaký ako zadaná hodnota. V prípade, ak je zhoda presunie sa na
     * nasledujúci token a zároveň vráti predchádzajúci token, inak vráti null;
     *
     * @param tag trieda tokenu
     *
     * @return porovnávaný token, inak null
     */
    private Leaf accept(byte tag) {
        if (getTokenTag() == tag) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            return terminal;
        }
        return null;
    }

    /**
     * Metóda pre zistenie, či aktuálny token je rovnaký ako zadaná hodnota. V prípade, ak je zhoda presunie sa na
     * nasledujúci token a zároveň vráti predchádzajúci token. V prípade, ak nie je zhoda, zistí sa chyba a následne sa
     * zistená chyba uloží do databázy chýb.
     *
     * @param tag trieda tokenu
     *
     * @return porovnávaný token, inak null
     */
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
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-01"), "E-SxA-01");
                break;
            case Tag.SEMICOLON:
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-03"), "E-SxA-03");
                break;
            case Tag.PLUS:
            case Tag.MINUS:
            case Tag.MULT:
            case Tag.DIV:
            case Tag.MOD:
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-05"), "E-SxA-05");
                break;
            case Tag.IDENTIFIER:
                if (getTokenTag() < 32) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("L-SxA-09"), "L-SxA-09");
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-06"), "E-SxA-06");
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
                        errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-02"), "E-SxA-02");
                        break;
                    default:
                        if (tag < 32 && getTokenTag() == Tag.IDENTIFIER) {
                            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-04"), "E-SxA-04");
                        } else {
                            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                        }
                }
                break;
        }
        return null;
    }

    /**
     * primary_expression:    IDENTIFIER
     *                      | constant
     *                      | string
     *                      | CHARACTER
     *                      | '(' expression ')'
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     */
    private Node primary_expression() {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
        int pos = position;
        Node child1;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                Record record = symbolTable.lookup(getTokenValue());
                if (record == null || (record.getKind() != Kind.ENUMERATION_CONSTANT && record.getKind() != Kind.TYPEDEF_NAME)) {
                    nextToken();
                    return new Identifier(getTokenValue(position - 1), getTokenLine(position - 1));
                }
                break;
            case Tag.CHARACTER:
                nextToken();
                return new Constant("char", getTokenValue(position - 1), getTokenLine(position - 1));
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
        child1 = string();
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * string:    STRING
     *          | string STRING
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     */
    private Node string() {
        StringBuilder str = new StringBuilder();
        if (getTokenTag() == Tag.STRING) {
            int line = getTokenLine();
            str.append(getTokenValue());
            nextToken();
            while (getTokenTag() == Tag.STRING) {
                str.append(getTokenValue());
                nextToken();
            }
            String stringLiteral = str.toString();
            stringLiteral = stringLiteral.replaceAll("\"", "");
            stringLiteral = "\"" + stringLiteral + "\"";
            return new Constant("string", stringLiteral, line);
        }
        return new None();
    }

    /**
     * constant:    NUMBER
     *            | REAL
     *            | ENUMERATION_CONSTANT
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     */
    private Node constant() {
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                nextToken();
                Record record = symbolTable.lookup(getTokenValue(position - 1));
                if (record != null && record.getKind() == Kind.ENUMERATION_CONSTANT) {
                    return new Constant("int", getTokenValue(position - 1), getTokenLine(position - 1));
                } else {
                    return new None();
                }

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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SmA-01"), "E-SmA-01");
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
     * enumeration_constant: IDENTIFIER
     * @return identifikátor, ak sa našla zhoda,
     *         prázdny reťazec, ak sa zhoda nenašla
     */
    private String enumeration_constant() {
        if (getTokenTag() == Tag.IDENTIFIER) {
            //vloženie do symbolickej tabuľky ako ENUMERATION_CONSTANT
            Record record = new Record(Type.INT, "int ", getTokenLine(), true, Kind.ENUMERATION_CONSTANT);
            symbolTable.insert(getTokenValue(), record, getTokenLine(), Kind.ENUMERATION_CONSTANT, errorDatabase);

            nextToken();
            return getTokenValue(position - 1);
        }
        return "";
    }

    /**
     * postfix_expression:    primary_expression rest1
     *                      | '(' type_name ')' '{' initializer_list '}' rest1
     *                      | '(' type_name ')' '{' initializer_list ',' '}' rest1
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     */
    private Node postfix_expression() {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
     * rest1:    '[' expression ']' rest1
     *         | '.' IDENTIFIER rest1
     *         | {@code '->'} IDENTIFIER rest1
     *         | '++' rest1
     *         | '--' rest1
     *         | '(' ')' rest1
     *         | '(' argument_expression_list ')' rest1
     *         | epsilon
     *
     * @param child vrchol z {@code postfix_expression}
     *
     * @return Node, ak sa našla zhoda,
     *         null, ak sa vyskytla chyba
     */
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
                    ref = new ArrayReference(child, child1, child.getLine(), symbolTable, errorDatabase);
                    child3 = rest1(ref);
                }
                if (child3 != null && !child3.isEmpty()) {
                    return child3;
                }
                if (child3 != null && child3.isEmpty()) {
                    return ref;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                ref = new UnaryOperator(child, terminal, child.getLine(), symbolTable, errorDatabase);
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
                    ref = new FunctionCall(child, null, child.getLine(), symbolTable);
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
                    ref = new FunctionCall(child, child1, child.getLine(), symbolTable);
                    child3 = rest1(ref);
                }
                if (child3 != null && !child3.isEmpty()) {
                    return child3;
                }
                if (child3 != null && child3.isEmpty()) {
                    return ref;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            default:
                return new EmptyStatement();
        }
    }

    /**
     * argument_expression_list:    argument_expression_list ',' assignment_expression
     *                            | assignment_expression
     * @return Node, ak sa našla zhoda,
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return new ExpressionList(arr, line, symbolTable, errorDatabase);
        }
        if (child1 != null && child1.isNone()) {
            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
        }
        return null;
    }

    /**
     * unary_expression:    postfix_expression
     *                    | '++' unary_expression
     *                    | '--' unary_expression
     *                    | unary_operator cast_expression
     *                    | SIZEOF '(' type_name ')'
     *                    | SIZEOF '(' unary_expression ')'
     *                    | SIZEOF unary_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    return new UnaryOperator(child1, terminal, child1.getLine(), symbolTable, errorDatabase);
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                        return new UnaryOperator(child1, terminal, child1.getLine(), symbolTable, errorDatabase);
                    }
                    child1 = unary_expression();
                    if (child1 != null && !child1.isNone()) {
                        child2 = expect(Tag.RIGHT_PARENTHESES);
                    }
                    if (child2 != null) {
                        return new UnaryOperator(child1, terminal, child1.getLine(), symbolTable, errorDatabase);
                    }
                    if (child1 != null && child1.isNone()) {
                        errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    }
                    return null;
                } else {
                    child1 = unary_expression();
                    if (child1 == null) {
                        return null;
                    }
                    if (!child1.isNone()) {
                        return new UnaryOperator(child1, terminal, child1.getLine(), symbolTable, errorDatabase);
                    } else {
                        errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                        return null;
                    }
                }
        }
        String operator = unary_operator();
        if (!operator.equals("")) {
            child1 = cast_expression();
            if (child1.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return new UnaryOperator(child1, operator, child1.getLine(), symbolTable, errorDatabase);
            }
        }
        child1 = postfix_expression();
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * {@code
     * unary_operator: '&' | '*' | '+' | '-' | '~' | '!'
     * }
     * @return hodnota aktuálneho tokenu, ak sa našla zhoda,
     *         prázdny reťazec, ak sa zhoda nenašla
     */
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
     * cast_expression:    unary_expression
     *                   | '(' type_name ')' cast_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     */
    private Node cast_expression() {
        Node child1, child2 = null, child3 = null;
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
                return new Cast(child1, child3, line, symbolTable, errorDatabase);
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
     * multiplicative_expression:    multiplicative_expression '*' cast_expression
     *                             | multiplicative_expression '/' cast_expression
     *                             | multiplicative_expression '%' cast_expression
     *                             | cast_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * additive_expression:    additive_expression '+' multiplicative_expression
     *                       | additive_expression '-' multiplicative_expression
     *                       | multiplicative_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * {@code
     * shift_expression:    shift_expression '<<' additive_expression
     *                    | shift_expression '>>' additive_expression
     *                    | additive_expression
     *  }
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * {@code
     * relational_expression:    relational_expression '<' shift_expression
     *                         | relational_expression '>' shift_expression
     *                         | relational_expression '<=' shift_expression
     *                         | relational_expression '>=' shift_expression
     *                         | shift_expression
     * }
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * equality_expression:    equality_expression '==' relational_expression
     *                       | equality_expression '!=' relational_expression
     *                       | relational_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * {@code
     * and_expression:    and_expression '&' equality_expression
     *                  | equality_expression
     * }
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * exclusive_or_expression:    exclusive_or_expression '^' and_expression
     *                           | and_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * inclusive_or_expression:    inclusive_or_expression '|' exclusive_or_expression
     *                           | exclusive_or_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * {@code
     * logical_and_expression:    logical_and_expression '&&' inclusive_or_expression
     *                          | inclusive_or_expression
     * }
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * logical_or_expression:    logical_or_expression '||' logical_and_expression
     *                         | logical_and_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child1 = new BinaryOperator(binOperator, terminal, child1, binOperator.getLine(), symbolTable, errorDatabase);
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return child1;
        }
        return new None();
    }

    /**
     * conditional_expression:    logical_or_expression
     *                          | logical_or_expression '?' expression ':' conditional_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    return new TernaryOperator(child1, child2, child4, child1.getLine(), symbolTable, errorDatabase);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                return child1;
            }
        }
        return new None();
    }

    /**
     * assignment_expression:    conditional_expression
     *                         | unary_expression assignment_operator assignment_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node assignment_expression() {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return new Assignment(child1, operator, child2, child1.getLine(), symbolTable, errorDatabase);
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
     * {@code
     * assignment_operator: '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<' | '>>=' | '&=' | '^=' | '|='
     * }
     *
     * @return hodnota aktuálneho tokenu, ak sa našla zhoda,
     *         prázdny reťazec, ak sa zhoda nenašla
     */
    private String assignment_operator() {
        Node child1 = accept(Tag.ASSIGNMENT);
        if (child1 != null) {
            return getTokenValue(position - 1);
        }
        return "";
    }

    /**
     * expression:    expression ',' assignment_expression
     *              | assignment_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return new ExpressionList(arr, line, symbolTable, errorDatabase);
        }
        return new None();
    }

    /**
     * constant_expression: conditional_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * declaration:    declaration_specifiers ';'
     *               | declaration_specifiers init_declarator_list ';'
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private ArrayList<Node> declaration() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = declaration_specifiers(typeNode);
        if (child1 == null) {
            //error recovery
            ArrayList<Node> arr = new ArrayList<>();
            Node err = errorRecoveryS();
            if (err == null) {
                return null;
            } else {
                arr.add(err);
                return arr;
            }
        }
        if (!child1.isNone()) {
            typeNode = (TypeNode) child1;
            if (getTokenTag() == Tag.SEMICOLON) {
                nextToken();
                if (typeNode.getTypes().size() == 1 && typeNode.getType(0).isEnumStructUnion()) {
                    ArrayList<Node> decls = new ArrayList<>();
                    decls.add(new Declaration(null, typeNode.getQualifiers(), typeNode.getStorage(), typeNode.getType(0),
                            null, 0));
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
                ArrayList<Node> arr = new ArrayList<>();
                Node err = errorRecoveryS();
                if (err == null) {
                    return null;
                } else {
                    arr.add(err);
                    return arr;
                }
            }
            if (!child2.isEmpty()) {
                child3 = expect(Tag.SEMICOLON);
            }
            if (child3 != null) {
                return createDeclaration(typeNode, child2);
            }
            //error recovery
            Node err = errorRecoverySB(true, Tag.SEMICOLON);
            if (err == null) {
                return null;
            } else {
                ArrayList<Node> arr = new ArrayList<>();
                arr.add(err);
                return arr;
            }
        }
        return new ArrayList<>();
    }

    /**
     * declaration_specifiers:    storage_class_specifier declaration_specifiers
     *                          | storage_class_specifier
     *                          | type_specifier declaration_specifiers
     *                          | type_specifier
     *                          | type_qualifier declaration_specifiers
     *                          | type_qualifier
     *
     * @param typeNode vrchol, do ktorého ukladáme informácie spojené s typom
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * init_declarator_list:   init_declarator_list ',' init_declarator
     *                       | init_declarator
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * init_declarator:    declarator '=' initializer
     *                   | declarator
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                if (getTokenValue().equals("==")) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
                return new InitDeclarator(child1, null);
            }
        }
        return new None();
    }

    /**
     * storage_class_specifier: TYPEDEF | EXTERN | STATIC | AUTO | REGISTER
     * @return hodnota aktuálneho tokenu, ak sa našla zhoda,
     *         prázdny reťazec, ak sa zhoda nenašla
     */
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
     * type_specifier:    VOID | CHAR | SHORT | INT | LONG | FLOAT | DOUBLE | SIGNED | UNSIGNED | SIZE_T | FILE
     *                  | struct_or_union_specifier
     *                  | enum_specifier
     *                  | TYPEDEF_NAME
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * struct_or_union_specifier:    struct_or_union '{' struct_declaration_list '}'
     *                             | struct_or_union IDENTIFIER '{' struct_declaration_list '}'
     *                             | struct_or_union IDENTIFIER
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                        return errorRecoveryB();
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
                        return errorRecoverySB(false, Tag.RIGHT_BRACES);
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
                            return errorRecoveryB();
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
                            return errorRecoverySB(false, Tag.RIGHT_BRACES);
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
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("L-SxA-09"), "L-SxA-09");
                return null;
            }
        }
        return new None();
    }

    /**
     * struct_or_union:  STRUCT | UNION
     * @return hodnota aktuálneho tokenu, ak sa našla zhoda,
     *         prázdny reťazec, ak sa zhoda nenašla
     */
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
     * struct_declaration_list:    struct_declaration_list struct_declaration
     *                           | struct_declaration
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * struct_declaration:    specifier_qualifier_list ';'
     *                      | specifier_qualifier_list struct_declarator_list ';'
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
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
                ArrayList<Node> arr = new ArrayList<>();
                Node err = errorRecoveryS();
                if (err == null) {
                    return null;
                } else {
                    arr.add(err);
                    return arr;
                }
            }
            if (!child2.isEmpty()) {
                child3 = expect(Tag.SEMICOLON);
                if (child3 == null) {
                    // error recovery
                    Node err = errorRecoverySB(false, Tag.SEMICOLON);
                    if (err == null) {
                        return null;
                    } else {
                        ArrayList<Node> arr = new ArrayList<>();
                        arr.add(err);
                        return arr;
                    }
                } else {
                    return createDeclaration(typeNode, child2, true);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * specifier_qualifier_list:    type_specifier specifier_qualifier_list
     *                            | type_specifier
     *                            | type_qualifier specifier_qualifier_list
     *                            | type_qualifier
     *
     * @param typeNode vrchol, do ktorého ukladáme informácie spojené s typom
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * struct_declarator_list:    struct_declarator_list ',' struct_declarator
     *                          | struct_declarator
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return arr;
        }
        return new ArrayList<>();
    }

    /**
     * struct_declarator:    ':' constant_expression
     *                     | declarator ':' constant_expression
     *                     | declarator
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node struct_declarator() {
        Node child1;
        if (getTokenTag() == Tag.COLON) {
            nextToken();
            child1 = constant_expression();
            if (child1 != null && !child1.isNone()) {
                return new StructDeclarator(new TypeDeclaration(null, null, null), child1);
            }
            if (child1 != null && child1.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                return new StructDeclarator(child1, null);
            }
        }
        return new None();
    }

    /**
     * enum_specifier: ENUM left1
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node enum_specifier() {
        if (getTokenTag() == Tag.ENUM) {
            nextToken();
            Node child1 = left1(getTokenLine(position - 1));
            if (child1 != null && !child1.isNone()) {
                return child1;
            }
            if (child1 != null && child1.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
            }
            return null;
        }
        return new None();
    }

    /**
     * left1:     '{' enumerator_list '}'
     *          | '{' enumerator_list ',' '}'
     *          | IDENTIFIER '{' enumerator_list '}'
     *          | IDENTIFIER '{' enumerator_list ',' '}'
     *          | IDENTIFIER
     *
     * @param line riadok využitia pre Enum
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left1(int line) {
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
                return errorRecoveryB();
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
                    return errorRecoveryB();
                } else {
                    return new Enum(terminal, null, line);
                }
        }
        if (getTokenTag() < 32) {
            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("L-SxA-09"), "L-SxA-09");
            return null;
        }
        return new None();
    }

    /**
     * enumerator_list:    enumerator_list ',' enumerator
     *                   | enumerator
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    position--;
                    return new EnumeratorList(arr, line);
                }
            }
            return new EnumeratorList(arr, line);
        }
        return new None();
    }

    /**
     * enumerator:    enumeration_constant '=' constant_expression
     *              | enumeration_constant
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            } else {
                return new Enumerator(child1, null, line);
            }
        }
        return new None();
    }

    /**
     * type_qualifier: CONST | VOLATILE
     * @return hodnota aktuálneho tokenu, ak sa našla zhoda,
     *         prázdny reťazec, ak sa zhoda nenašla
     */
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
     * declarator:    pointer direct_declarator
     *              | direct_declarator
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * direct_declarator:    IDENTIFIER rest2
     *                     | '(' declarator ')' rest2
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node direct_declarator() {
        Node child1, child2 = null, child3 = null;
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
        int pos = position;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                String terminal = getTokenValue();
                nextToken();
                Node declarator = new TypeDeclaration(terminal, null, null, getTokenLine(position - 1));
                child1 = rest2(declarator);
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
                    child3 = rest2(child1);
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

        if (getTokenTag() < 32) {
            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("L-SxA-09"), "L-SxA-09");
            return null;
        }
        return new None();
    }

    /**
     * rest2:    '[' left2
     *          | '(' left3
     *          | epsilon
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         null, ak sa vyskytla chyba
     */
    private Node rest2(Node declarator) {
        Node child1;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = left2(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = left3(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            default:
                return new EmptyStatement();
        }
    }

    /**
     * left2:    '*' ']' rest2
     *          | STATIC left4
     *          | type_qualifier_list left5
     *          | assignment_expression ']' rest2
     *          | ']' rest2
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left2(Node declarator) {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
                    child2 = rest2(child);
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
                child1 = left4(declarator);
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    return child1;
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            case Tag.RIGHT_BRACKETS:
                nextToken();
                decl = new ArrayDeclaration(null, null, new ArrayList<>(), getTokenLine(position - 1));
                Node child = modifyType(declarator, decl);
                child1 = rest2(child);
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
                child3 = rest2(child);
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
            child2 = left5(child4, declarator);
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return child2;
            }
        }
        return new None();
    }

    /**
     * left3:    parameter_type_list ')' rest2
     *          | ')' rest2
     *          | identifier_list rest2
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left3(Node declarator) {
        Node child1, decl, child;
        if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
            nextToken();
            decl = new FunctionDeclaration(null, null, getTokenLine(position - 1));
            child = modifyType(declarator, decl);
            child1 = rest2(child);
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
                child3 = rest2(child);
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
            child2 = rest2(child);
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
     * left4:    type_qualifier_list assignment_expression ']' rest2
     *          | assignment_expression ']' rest2
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
      private Node left4(Node declarator) {
        ArrayList<String> child1 = type_qualifier_list();
        Node decl, child2, child3, child4;
        if (!child1.isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                    child4 = rest2(child);
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
                child4 = rest2(child);
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
     * left5:    '*' ']' rest2
     *          | STATIC assignment_expression ']' rest2
     *          | assignment_expression ']' rest2
     *          | ']' rest2
     *
     * @param qualifiers zoznam kvalifikátorov (static,...)
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left5(ArrayList<String> qualifiers, Node declarator) {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
                    child2 = rest2(child);
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    qualifiers.add("static");

                    decl = new ArrayDeclaration(null, child1, qualifiers, getTokenLine(position - 1));
                    child = modifyType(declarator, decl);
                    child3 = rest2(child);
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
                child1 = rest2(child);
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
                child3 = rest2(child);
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
     * pointer:    '*' type_qualifier_list pointer
     *           | '*' type_qualifier_list
     *           | '*' pointer
     *           | '*'
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     */
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
     * type_qualifier_list:    type_qualifier_list type_qualifier
     *                       | type_qualifier
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     */
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
     * parameter_type_list:    parameter_list ',' '...'
     *                       | parameter_list
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * parameter_list:    parameter_list ',' parameter_declaration
     *                  | parameter_declaration
     * @return ParameterList, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                   return new ParameterList(arr, line, symbolTable, errorDatabase);
                }
            }
            return new ParameterList(arr, line, symbolTable, errorDatabase);
        }
        return new ParameterList(arr, 0, symbolTable, errorDatabase);
    }

    /**
     * parameter_declaration:   declaration_specifiers left6
     *                         | left6
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node parameter_declaration() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = declaration_specifiers(typeNode);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            typeNode = (TypeNode) child1;
            child2 = left6(typeNode);
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return child2;
            }
        }
        typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        child1 = left6(typeNode);
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        return new None();
    }

    /**
     * left6:    declarator
     *          | abstract_declarator
     *          | epsilon
     *
     * @param typeNode vrchol, do ktorého ukladáme informácie spojené s typom
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
      private Node left6(TypeNode typeNode) {
        if (typeNode.getTypes().isEmpty()) {
            ArrayList<String> arr = new ArrayList<>();
            arr.add("int");
            typeNode.addType(new IdentifierType(arr, 0));
        }

        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
        int pos = position;
        Node child1 = declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            if (typeNode.getTypes().size() == 0) {
                typeNode.addType(new IdentifierType(new ArrayList<>(Collections.singletonList("int")), child1.getLine()));
            }
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
            if (typeNode.getTypes().size() == 0) {
                typeNode.addType(new IdentifierType(new ArrayList<>(Collections.singletonList("int")), child1.getLine()));
            }
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

        if (typeNode.getTypes().size() == 0) {
            typeNode.addType(new IdentifierType(new ArrayList<>(Collections.singletonList("int")), child1.getLine()));
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
     * identifier_list:    identifier_list ',' IDENTIFIER
     *                   | IDENTIFIER
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
            return new ParameterList(arr, line, symbolTable, errorDatabase);
        }
        if (getTokenTag() < 32) {
            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("L-SxA-09"), "L-SxA-09");
            return null;
        }
        return new None();
    }

    /**
     * type_name:    specifier_qualifier_list abstract_declarator
     *             | specifier_qualifier_list
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * abstract_declarator:    pointer direct_abstract_declarator
     *                       | pointer
     *                       | direct_abstract_declarator
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * direct_abstract_declarator:    '(' left7
     *                              | '[' left8
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node direct_abstract_declarator() {
        Node child1;
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
        int pos = position;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = left7();
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                position = pos;
                symbolTable = copySymbolTable;
                errorDatabase = copyErrorDatabase;
                return new None();
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = left8(null);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * left7:    abstract_declarator ')' rest3
     *          | ')' rest3
     *          | parameter_type_list ')' rest3
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left7() {
        Node child1, decl;
        if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
            nextToken();
            decl = new FunctionDeclaration(null, null, getTokenLine(position - 1));
            child1 = rest3(decl);
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
                child3 = rest3(child1);
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
                child3 = rest3(decl);
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
     * left8:    ']' rest3
     *          | '*' ']' rest3
     *          | STATIC left9
     *          | type_qualifier_list left10
     *          | assignment_expression ']' rest3
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left8(Node declarator) {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
                child1 = rest3(child);
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
                    child2 = rest3(child);
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
                child1 = left9(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                child3 = rest3(child);
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
            child2 = left10(child4, declarator);
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                return child2;
            }
        }
        return new None();
    }

    /**
     * left9:    type_qualifier_list assignment_expression ']' rest3
     *          | assignment_expression ']' rest3
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left9(Node declarator) {
        ArrayList<String> child1 = type_qualifier_list();
        Node child2, child3, child4, decl, child;
        if (!child1.isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null) {
                return null;
            }
            if (child2.isNone()) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                    child4 = rest3(child);
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
                child4 = rest3(child);
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
     * left10:    STATIC assignment_expression ']' rest3
     *          | assignment_expression ']' rest3
     *          | ']' rest3
     *
     * @param qualifiers zoznam kvalifikátorov (static, ...)
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left10(ArrayList<String> qualifiers, Node declarator) {
        Node child1, child2 = null, child3 = null, decl, child = null;
        switch (getTokenTag()) {
            case Tag.STATIC:
                nextToken();
                child1 = assignment_expression();
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                    child3 = rest3(child);
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
                child1 = rest3(child);
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
                child3 = rest3(child);
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
     * rest3:    '[' left28
     *          | '(' left11
     *          | epsilon
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         null, ak sa vyskytla chyba
     */
    private Node rest3(Node declarator) {
        Node child1;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = left8(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = left11(declarator);
                if (child1 != null && !child1.isNone()) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
            default:
                return new EmptyStatement();
        }
    }

    /**
     * left11:    ')' rest3
     *          | parameter_type_list ')' rest3
     *
     * @param declarator vrchol z metódy, z ktorej bol volaný
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left11(Node declarator) {
        Node child1, decl, child;
        if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
            nextToken();
            decl = new FunctionDeclaration(null, null, getTokenLine(position - 1));
            child = modifyType(declarator, decl);
            child1 = rest3(child);
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
                child3 = rest3(child);
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
     * initializer:    '{' initializer_list '}'
     *               | '{' initializer_list ',' '}'
     *               | '{' '}'
     *               | assignment_expression
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node initializer() {
        Node child1, child2;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            nextToken();
            if (getTokenTag() == Tag.RIGHT_BRACES) {
                nextToken();
                return new InitializationList(new ArrayList<>(), getTokenLine(position - 1), symbolTable, errorDatabase);
            }
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
            return errorRecoveryB();
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
     * initializer_list:    designation initializer rest23
     *                    | initializer rest23
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                ArrayList<Node> arr = new ArrayList<>();
                arr.add(new NamedInitializer(child1, child2, symbolTable, errorDatabase));
                InitializationList init = new InitializationList(arr, child2.getLine(), symbolTable, errorDatabase);
                child3 = rest4(init);
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
            InitializationList init = new InitializationList(arr, child2.getLine(), symbolTable, errorDatabase);
            child3 = rest4(init);
            if (child3 == null) {
                return null;
            } else {
                return init;
            }
        }
        return new None();
    }

    /**
     * rest4:    ',' designation initializer rest4
     *          | ',' initializer rest4
     *          | epsilon
     *
     * @param init list s inicializovanými hodnotami
     *
     * @return Node, ak sa našla zhoda,
     *         null, ak sa vyskytla chyba
     */
    private Node rest4(InitializationList init) {
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                } else {
                    init.addExpression(new NamedInitializer(child1, child2, symbolTable, errorDatabase));
                    child3 = rest4(init);
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
                child3 = rest4(init);
                if (child3 == null) {
                    return null;
                } else {
                    return new EmptyStatement();
                }
            }
            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
            return null;
        }
        return new EmptyStatement();
    }

    /**
     * designation: designator_list '='
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            }
        }
        return new ArrayList<>();
    }

    /**
     * designator_list:    designator_list designator
     *                   | designator
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * designator:    '[' constant_expression ']'
     *              | '.' IDENTIFIER
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
     * statement:    labeled_statement
     *             | compound_statement
     *             | expression_statement
     *             | selection_statement
     *             | iteration_statement
     *             | jump_statement
     *
     * @param createSymbolTable informácia o tom, či sa má vytvoriť symbolická tabuľka
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node statement(boolean createSymbolTable) {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
        child1 = compound_statement(createSymbolTable, false);
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
     * labeled_statement:    IDENTIFIER ':' statement
     *                     | CASE constant_expression ':' statement
     *                     | DEFAULT ':' statement
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node labeled_statement() {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
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
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                    return new Case(child1, child3, line, symbolTable, errorDatabase);
                }
                if ((child1 != null && child1.isNone()) || (child3 != null && child3.isNone())) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * compound_statement:    '{' '}'
     *                      | '{' block_item_list '}'
     *
     * @param createSymbolTable informácia o tom, či sa má vytvoriť symbolická tabuľka
     *
     * @param functionDefinition informácia o tom, či ide o definíciu funkcie
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node compound_statement(boolean createSymbolTable, boolean functionDefinition) {
        if (getTokenTag() == Tag.LEFT_BRACES) {
            int line = getTokenLine();
            nextToken();
            if (getTokenTag() == Tag.RIGHT_BRACES) {
                lastStatementLine = getTokenLine();
                nextToken();
                return new Compound(null, line);
            }

            if (createSymbolTable && !functionDefinition) {
                //vytvorenie vnorenej tabuľky
                symbolTable = new SymbolTable(symbolTable);
                if (symbolTable.getParent() != null) {
                    symbolTable.getParent().addChild(symbolTable);
                }
            }

            ArrayList<Node> child1 = block_item_list();
            Node child2;
            if (child1 == null) {
                //error recovery
                return errorRecoveryB();
            }
            if (!child1.isEmpty()) {
                child2 = expect(Tag.RIGHT_BRACES);
                if (child2 == null) {
                    //error recovery
                    return errorRecoveryB();
                } else {
                    lastStatementLine = getTokenLine(position - 1);
                    if (createSymbolTable) {
                        symbolTable = symbolTable.getParent();
                    }
                    return new Compound(child1, line);
                }
            }
        }
        return new None();
    }

    /**
     * block_item_list:    block_item_list block_item
     *                   | block_item
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * block_item:    declaration
     *              | statement
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * expression_statement:    ';'
     *                        | expression ';'
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node expression_statement() {
        if (getTokenTag() == Tag.SEMICOLON) {
            lastStatementLine = getTokenLine();
            nextToken();
            return new EmptyStatement();
        }
        Node child1 = expression();
        Node child2;
        if (child1 == null) {
            //error recovery
            return errorRecoveryS();
        }
        if (!child1.isNone()) {
            child2 = expect(Tag.SEMICOLON);
            if (child2 == null) {
                //error recovery
                return errorRecoverySB(false, Tag.SEMICOLON);
            } else {
                lastStatementLine = getTokenLine(position - 1);
                return child1;
            }
        }
        return new None();
    }

    /**
     * selection_statement:    IF '(' expression ')' statement ELSE statement
     *                       | IF '(' expression ')' statement
     *                       | SWITCH '(' expression ')' statement
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                            return new If(child2, child4, child5, line, symbolTable, errorDatabase);
                        }
                    } else {
                        return new If(child2, child4, null, line, symbolTable, errorDatabase);
                    }
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())
                        || (child5 != null && child5.isNone())) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                    return new Switch(child2, child4, line, symbolTable, errorDatabase);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * iteration_statement:    WHILE '(' expression ')' statement
     *                       | DO statement WHILE '(' expression ')' ';'
     *                       | FOR '(' left12
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    child2.resolveUsage(symbolTable, lastStatementLine);
                    return new While(child2, child4, line, symbolTable, errorDatabase);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                    return errorRecoveryS();
                }
                if (child6 != null) {
                    lastStatementLine = getTokenLine(position - 1);
                    return new DoWhile(child4, child1, line, symbolTable, errorDatabase);
                } else {
                    //error recovery
                    return errorRecoverySB(false, Tag.SEMICOLON);
                }
            case Tag.FOR:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.LEFT_PARENTHESES);
                if (child1 != null) {
                    child2 = left12(line);
                }
                if (child2 == null) {
                    return null;
                }
                if (!child2.isNone()) {
                    return child2;
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                }
                return null;
        }
        return new None();
    }

    /**
     * left12:    expression_statement expression_statement ')' statement
     *          | expression_statement expression_statement expression ')' statement
     *          | declaration expression_statement ')' statement
     *          | declaration expression_statement expression ')' statement
     *
     * @param line riadok využitia pre for cyklus
     *
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private Node left12(int line) {
        //vytvorenie vnorenej tabuľky
        symbolTable = new SymbolTable(symbolTable);
        if (symbolTable.getParent() != null) {
            symbolTable.getParent().addChild(symbolTable);
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
                 errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                 if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
                     nextToken();
                     child3 = statement(false);
                     symbolTable = symbolTable.getParent();
                     if (child3 != null && !child3.isNone()) {
                         if (child1.isEmpty()) {
                             child1 = null;
                         }
                         if (child2.isEmpty()) {
                             child2 = null;
                         } else {
                             child2.resolveUsage(symbolTable, lastStatementLine);
                         }
                         return new For(child1, child2, null, child3, line, symbolTable, errorDatabase);
                     }
                     if (child3 != null && child3.isNone()) {
                         errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                         symbolTable = symbolTable.getParent();
                         if (child5 == null) {
                             return null;
                         }
                         if (child5.isNone()) {
                             errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                             return null;
                         } else {
                             if (child1.isEmpty()) {
                                 child1 = null;
                             }
                             if (child2.isEmpty()) {
                                 child2 = null;
                             } else{
                                 child2.resolveUsage(symbolTable, lastStatementLine);
                             }
                             child3.resolveUsage(symbolTable, lastStatementLine);
                             return new For(child1, child2, child3, child5, line, symbolTable, errorDatabase);
                         }
                     }
                 } else {
                     errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                return null;
            } else {
                if (getTokenTag() == Tag.RIGHT_PARENTHESES) {
                    nextToken();
                    child3 = statement(false);
                    symbolTable = symbolTable.getParent();
                    if (child3 != null && !child3.isNone()) {
                        if (child2.isEmpty()) {
                            child2 = null;
                        } else {
                            child2.resolveUsage(symbolTable, lastStatementLine);
                        }
                        return new For(new DeclarationList(child, line), child2, null, child3, line, symbolTable, errorDatabase);
                    }
                    if (child3 != null && child3.isNone()) {
                        errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
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
                        symbolTable = symbolTable.getParent();
                        if (child5 == null) {
                            return null;
                        }
                        if (child5.isNone()) {
                            errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                            return null;
                        } else {
                            if (child2.isEmpty()) {
                                child2 = null;
                            } else {
                                child2.resolveUsage(symbolTable, lastStatementLine);
                            }
                            child3.resolveUsage(symbolTable, lastStatementLine);
                            return new For(new DeclarationList(child, line), child2, child3, child5, line, symbolTable, errorDatabase);
                        }
                    }
                } else {
                    errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
                    return null;
                }
            }
        }
        return new None();
    }

    /**
     * jump_statement:    GOTO IDENTIFIER ';'
     *                  | CONTINUE ';'
     *                  | BREAK ';'
     *                  | RETURN ';'
     *                  | RETURN expression ';'
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
                    return errorRecoveryS();
                } else {
                    terminal = getTokenValue(position - 1);
                    child2 = expect(Tag.SEMICOLON);
                }
                if (child2 != null) {
                    return new Goto(terminal, line);
                } else {
                    //error recovery
                    return errorRecoverySB(false, Tag.SEMICOLON);
                }
            case Tag.CONTINUE:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.SEMICOLON);
                if (child1 != null) {
                    return new Continue(line);
                } else {
                    //error recovery
                    return errorRecoverySB(false, Tag.SEMICOLON);
                }
            case Tag.BREAK:
                line = getTokenLine();
                nextToken();
                child1 = expect(Tag.SEMICOLON);
                if (child1 != null) {
                    return new Break(line);
                } else {
                    //error recovery
                    return errorRecoverySB(false, Tag.SEMICOLON);
                }
            case Tag.RETURN:
                line = getTokenLine();
                nextToken();
                if (getTokenTag() == Tag.SEMICOLON) {
                    nextToken();
                    return new Return(null, line, symbolTable, errorDatabase);
                }
                child1 = expression();
                if (child1 == null) {
                    //error recovery
                    return errorRecoveryS();
                }
                if (!child1.isNone()) {
                    child2 = expect(Tag.SEMICOLON);
                    if (child2 == null) {
                        //error recovery
                        return errorRecoverySB(false, Tag.SEMICOLON);
                    } else {
                        return new Return(child1, line, symbolTable, errorDatabase);
                    }
                }
                return null;
        }
        return new None();
    }

    /**
     * translation_unit:    translation_unit external_declaration
     *                    | external_declaration
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázdny zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * external_declaration:    function_definition
     *                        | declaration
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázndy zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
    private ArrayList<Node> external_declaration() {
        SymbolTable copySymbolTable = symbolTable.createCopy();
        ErrorDatabase copyErrorDatabase = errorDatabase.createCopy();
        int pos = position;
        Node child1 = function_definition();
        if (!child1.isNone()) {
            ArrayList<Node> arr = new ArrayList<>();
            arr.add(child1);
            return arr;
        }
        position = pos;
        symbolTable = copySymbolTable;
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
     * function_definition:    declaration_specifiers declarator declaration_list compound_statement
     *                       | declaration_specifiers declarator compound_statement
     *                       | declarator declaration_list compound_statement
     *                       | declarator compound_statement
     * @return Node, ak sa našla zhoda,
     *         None, ak sa zhoda nenašla
     */
    private Node function_definition() {
        TypeNode typeNode = new TypeNode(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Node child1 = declaration_specifiers(typeNode);
        Node child2 = null, child3;

        symbolTable = new SymbolTable(symbolTable);
        if (symbolTable.getParent() != null) {
            symbolTable.getParent().addChild(symbolTable);
        }

        if (child1 != null && !child1.isNone()) {
            child2 = declarator();
        }
        if (child2 != null && !child2.isNone()) {
            typeNode = (TypeNode) child1;
            ArrayList<Node> child = declaration_list();
            Node child4 = null;
            if (child != null && !child.isEmpty()) {
                child4 = compound_statement(true, true);
            }
            if (child4 != null && !child4.isNone()) {
                return createFunction(typeNode, child2, child, child4);
            }
            child3 = compound_statement(true, true);
            if (child3 != null && !child3.isNone()) {
                return createFunction(typeNode, child2, null, child3);
            }
        }

        child1 = declarator();
        if (child1 != null && !child1.isNone()) {
            ArrayList<Node> child = declaration_list();
            child2 = null;
            if (child != null && !child.isEmpty()) {
                child2 = compound_statement(true, true);
            }
            if (child2 != null && !child2.isNone()) {
                ArrayList<Node> types = new ArrayList<>();
                types.add(new IdentifierType(new ArrayList<>(Collections.singletonList("int")), child1.getLine()));
                typeNode = new TypeNode(types, new ArrayList<>(), new ArrayList<>());
                return createFunction(typeNode, child1, child, child2);
            }
            child2 = compound_statement(true, true);
            if (child2 != null && !child2.isNone()) {
                ArrayList<Node> types = new ArrayList<>();
                types.add(new IdentifierType(new ArrayList<>(Collections.singletonList("int")), child1.getLine()));
                typeNode = new TypeNode(types, new ArrayList<>(), new ArrayList<>());
                return createFunction(typeNode, child1, null, child2);
            }
        }
        return new None();
    }

    /**
     * declaration_list:    declaration_list declaration
     *                    | declaration
     * @return zoznam vrcholov (Node), ak sa našla zhoda,
     *         prázndy zoznam, ak sa zhoda nenašla
     *         null, ak sa vyskytla chyba
     */
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
     * Metóda pre vytvorenie deklarácie na základe zadaného typu.
     *
     * <p> Táto metóda preťažuje metódu {@code createDeclaration} s tým, že argumenty parameter a structVariable majú
     * hodnotu false.
     *
     * @param typeNode vrchol pre typ deklarácie
     *
     * @param declarations zoznam vrcholov deklarovaných premenných
     *
     * @return zoznam vrcholov zadeklarovaných premenných
     */
    private ArrayList<Node> createDeclaration(TypeNode typeNode, ArrayList<Node> declarations) {
        return createDeclaration(typeNode, declarations, false, false);
    }

    /**
     * Metóda pre vytvorenie deklarácie na základe zadaného typu.
     *
     * <p> Táto metóda preťažuje metódu {@code createDeclaration} s tým, že argument parameter má hodnotu false.
     *
     * @param typeNode vrchol pre typ deklarácie
     *
     * @param declarations zoznam vrcholov deklarovaných premenných
     *
     * @param structVariable informácia, či ide o premennú v rámci štruktúry
     *
     * @return zoznam vrcholov zadeklarovaných premenných
     */
    private ArrayList<Node> createDeclaration(TypeNode typeNode, ArrayList<Node> declarations, boolean structVariable) {
        return createDeclaration(typeNode, declarations, false, structVariable);
    }

    /**
     * Metóda pre vytvorenie deklarácie na základe zadaného typu.
     *
     * <p> Táto metóda vytvára deklaráciu premenných. umožňuje vytvárať aj také deklarácie, ktoré sa nachádzajú na
     * jednom riadku. Počas deklarácie sa jednotlivé premenné pridaávajú aj do symbolickej tabuľky. V tejto metóda
     * prebieha aj kontrola typov v prípade, ak deklarovanú hodnotu inicializujeme.
     *
     * @param typeNode vrchol pre typ deklarácie
     *
     * @param declarations zoznam vrcholov deklarovaných premenných
     *
     * @param parameter informácia, či ide o parameter funkcie
     *
     * @param structVariable informácia, či ide o premennú v rámci štruktúry
     *
     * @return zoznam vrcholov zadeklarovaných premenných
     */
    private ArrayList<Node> createDeclaration(TypeNode typeNode, ArrayList<Node> declarations, boolean parameter,
                                              boolean structVariable) {
        ArrayList<Node> decls = new ArrayList<>();

        boolean typedef = typeNode.getStorage().contains("typedef");

        Declarator declarator = (Declarator) declarations.get(0);

        if (declarator.getDeclarator() == null) {
            declarator.addDeclarator(new TypeDeclaration(((IdentifierType) typeNode.getLastType()).getName(0),
                    null, null, typeNode.getLine()));
            typeNode.removeLastType();
        } else if (!declarator.getDeclarator().isEnumStructUnion() && !(declarator.getDeclarator() instanceof IdentifierType)) {
            Node tail = declarator.getDeclarator();

            while (!(tail instanceof TypeDeclaration)) {
                tail = tail.getType();
            }

            if (((TypeDeclaration) tail).getDeclname() == null) {
                String name = ((IdentifierType) typeNode.getLastType()).getName(0);
                ((TypeDeclaration) tail).addDeclname(name);
                typeNode.removeLastType();
            }
        }

        Node declaration;
        for (Node decl : declarations) {
            Declarator declarator1 = (Declarator) decl;
            if (declarator1.getDeclarator() == null) return null;
            if (typedef) {
                declaration = new Typedef(null, typeNode.getQualifiers(), typeNode.getStorage(),
                        declarator1.getDeclarator(), declarator1.getDeclarator().getLine());
            } else {
                declaration = new Declaration(null, typeNode.getQualifiers(), typeNode.getStorage(),
                        declarator1.getDeclarator(), declarator1.getInitializer(),
                        declarator1.getDeclarator().getLine());
            }
            if (!declaration.getType().isEnumStructUnion() && !(declaration.getType() instanceof IdentifierType)) {
                declaration = fixTypes(declaration, typeNode.getTypes());
            }

            if (!typeCheck(((Declarator) decl).getDeclarator(),((Declarator) decl).getInitializer())) {
                errorDatabase.addErrorMessage(declarator1.getInitializer().getLine(), InternationalizationClass.getErrors().getString("E-SmA-01"), "E-SmA-01");
            } else if (((Declarator) decl).getDeclarator() instanceof Identifier) {
                Declarator declarator2 = (Declarator) decl;
                Record record = symbolTable.lookup(((Identifier) declarator2.getDeclarator()).getName());
                if (record != null && (record.getKind() == Kind.ARRAY || record.getKind() == Kind.ARRAY_PARAMETER ||
                        record.getKind() == Kind.STRUCT_ARRAY_PARAMETER) &&
                        TypeChecker.findTypeCategory(declarator2.getInitializer(), symbolTable) > 50) {
                    errorDatabase.addErrorMessage(declarator2.getLine(), InternationalizationClass.getErrors().getString("E-RP-08"), "E-RP-08");
                }
            }

            SymbolTableFiller.addRecordToSymbolTable(declarator1, typedef, parameter, structVariable, symbolTable, errorDatabase);

            if (declarator1.getInitializer() != null) {
                SymbolTableFiller.resolveUsage(declarator1.getInitializer(), symbolTable, errorDatabase, true, true);
            }

            decls.add(declaration);
        }

        return decls;
    }

    /**
     * Metóda pre nastavenie názvu identifikátora pre vonkajší vrchol a zároveň nastavenie typu pre najvnútornejší vrchol.
     *
     * @param declaration vonkajší vrchol, pre ktorý sa napravuje typ
     *
     * @param typename zoznam typových špecifikátorov (int, long, ...)
     *
     * @return upravený vrchol
     */
    private Node fixTypes(Node declaration, ArrayList<Node> typename) {
        DeclarationNode decl = (DeclarationNode) declaration;

        Node type = declaration;
        while (!(type instanceof TypeDeclaration)) {
            type = type.getType();
        }

        //
        // Z vnútorného vrcholu, kde sa nachádza názov identifikátora, sa tento názov identifikátoru vloží do
        // vonkajšieho vrcholu (DeclarationNode).
        //
        decl.addName(((TypeDeclaration) type).getDeclname());
        ((TypeDeclaration) type).addQualifiers(decl.getQualifiers());

        //
        // Riešenie, či v typoch sú len vrcholy IdentifierType, ak nie ide o Struct, Enum alebo Union, ktorý musí byť
        // ako samostantý typ.
        //
        for (Node t_name : typename) {
            if (!(t_name instanceof IdentifierType)) {
                if (typename.size() > 1) {
                    errorDatabase.addErrorMessage(decl.getLine(), InternationalizationClass.getErrors().getString("E-SmA-01"), "E-SmA-01");
                    return null;
                } else {
                    type.addType(t_name);
                    return decl;
                }
            }
        }
        if (typename.isEmpty()) {
            // ak nie je žiaden typ, nastavuje sa defaultne int
            if (!(decl.getType() instanceof FunctionDeclaration)) {
                errorDatabase.addErrorMessage(decl.getLine(), InternationalizationClass.getErrors().getString("E-SmA-01"), "E-SmA-01");
                return null;
            }
            ArrayList<String> arr = new ArrayList<>();
            arr.add("int");
            type.addType(new IdentifierType(arr, decl.getLine()));
        } else {
            // každý typový špecifikátor je v IdentifierType a je potrebné  všetky tieto typy zjednotiť a obaliť ich
            // iba v jednom IdentifierType
            ArrayList<String> arr = new ArrayList<>();
            for (Node t_name : typename) {
                arr.addAll(((IdentifierType) t_name).getNames());
            }

            type.addType(new IdentifierType(arr, typename.get(0).getLine()));
        }

        return decl;
    }

    /**
     * Metóda pre pridanie typu pre novopridávaný vrchol (obalový vrchol).
     *
     * @param declaration existujúci vrchol
     *
     * @param modifier novo pridávaný vrchol
     *
     * @return výsledný vrchol
     */
    private Node modifyType(Node declaration, Node modifier) {
        Node tail = modifier;

        while (tail.getType() != null) {
            tail = tail.getType();
        }

        if (declaration instanceof TypeDeclaration) {
            // nastavujeme typ pre modifier (obalový typ)
            tail.addType(declaration);
            return modifier;
        } else {
            // ak declaration nie je základný typ, nájdem jeho výsledný typ a priradíme ho k vrcholu modifier
            Node declaration_tail = declaration;

            while (!(declaration_tail.getType() instanceof TypeDeclaration)) {
                declaration_tail = declaration_tail.getType();
            }

            tail.addType(declaration_tail.getType());
            declaration_tail.addType(modifier);

            return declaration;
        }
    }

    /**
     * Metóda pre vytvorenie deklarácie funkcie na základe zadaného typu.
     *
     * @param typeNode vrchol pre typ deklarácie
     *
     * @param declaration vrchol deklarovanej funkcie
     *
     * @param parameters zoznam parametrov funckie
     *
     * @param body telo funckie
     *
     * @return vrchol pre zadefinovanú funkciu
     */
    private Node createFunction(TypeNode typeNode, Node declaration, ArrayList<Node> parameters, Node body) {
        ArrayList<Node> arr = new ArrayList<>();
        arr.add(new Declarator(declaration, null));
        Node decl = createDeclaration(typeNode, arr).get(0);

        return new FunctionDefinition(decl, parameters, body, decl.getLine());
    }

    /**
     * Metóda pre typovú kontrolu priradenia pri deklarovaní premennej.
     *
     * @param declarator ľavá časť priradenia
     *
     * @param initializer pravá časť priradenia
     *
     * @return true, ak nie je typová nezhoda
     *         false, ak je typová nezhoda
     */
    private boolean typeCheck(Node declarator, Node initializer) {
        if (initializer == null || declarator == null) {
            return true;
        }

        if (initializer instanceof InitializationList) {
            return true;
        }

        if (declarator instanceof TypeDeclaration) {
            Node tail = declarator.getType();
            String type = "";
            int pointer = 0;

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
                    pointer++;
                }
                tail = tail.getType();
            }

            if (type.equals("")) {
                type = String.join(" ", ((IdentifierType) tail).getNames()) + " ";
            }
            type = TypeChecker.addPointers(pointer, type);

            short type1 = TypeChecker.findType(type, tail, symbolTable);
            short type2 = TypeChecker.getInitializer(initializer, symbolTable);

            if (type2 == -2) {
                return true;
            }

            if (type1 == -1 || type2 < 0) {
                return false;
            }

            if (type1 == Type.VOID || type2 == Type.VOID) {
                return false;
            }

            if (type1 == type2) {
                return true;
            }

            type1 = (type1 >= 50) ? (short) (type1 % 50) : type1;
            type2 = (type2 >= 50) ? (short) (type2 % 50) : type2;

            if (type1 == type2) {
                return true;
            }

            if (pointer > 0 && (type1 == Type.CHAR || type1 == Type.SIGNEDCHAR || type1 == Type.UNSIGNEDCHAR) && type2 == Type.STRING) {
                return true;
            }

            if (type1 >= Type.UNION && type2 >= Type.UNION) {
                return false;
            }

            return true;
        } else if (declarator instanceof ArrayDeclaration) {
            Node tail = declarator.getType();
            String type = "";
            int pointer = 0;

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
                    pointer++;
                }
                tail = tail.getType();
            }

            if (type.equals("")) {
                type = String.join(" ", ((IdentifierType) tail).getNames()) + " ";
            }
            type = TypeChecker.addPointers(pointer, type);

            short type1 = TypeChecker.findType(type, tail, symbolTable);
            short type2 = TypeChecker.getInitializer(initializer, symbolTable);

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
            int pointer = 1;

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
                    pointer++;
                }
                tail = tail.getType();
            }

            if (type.equals("")) {
                type = String.join(" ", ((IdentifierType) tail).getNames()) + " ";
            }
            type = TypeChecker.addPointers(pointer, type);

            short type1 = TypeChecker.findType(type, tail, symbolTable);
            short type2 = TypeChecker.getInitializer(initializer, symbolTable);

            if (type2 == -2) {
                return true;
            }

            if (type2 < 0 || type1 == -1) {
                return false;
            }
            if (type1 == type2) {
                return true;
            }

            type1 = (type1 >= 50) ? (short) (type1 % 50) : type1;
            type2 = (type2 >= 50) ? (short) (type2 % 50) : type2;

            if (type1 == type2) {
                return true;
            }

            if ((type1 == Type.CHAR || type1 == Type.SIGNEDCHAR || type1 == Type.UNSIGNEDCHAR) && type2 == Type.STRING) {
                return true;
            }
            return true;
        } else {
            return true;
        }

    }

    /**
     * Metóda pre vysporiadanie sa z chybového stavu.
     *
     * <p> V rámci tejto metódy sa postupuje tak, že sa prechádzajú tokeny, až kým sa nenájde záchytný bod, pri tejto
     * metóde ide o bodkočiarku ';' a pravú kučeravú zátvorku '}'.
     *
     * @param checkError informácia o tom, či sa má chyba zapisovať
     *
     * @param tag trieda tokenu, ktorý má bť prvý nájdený
     *
     * @return Err, ak sa program zotavil z chyby
     *         null, ak sa program nezotavil z chyby
     */
    private Node errorRecoverySB(boolean checkError, byte tag) {
        while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
            nextToken();
            //ak je koniec súboru
            if (position == tokenStream.size() - 1) {
                return null;
            }
        }
        if (getTokenTag() == tag) {
            nextToken();
            return new Err();
        } else {
            if (checkError) {
                errorDatabase.addErrorMessage(getTokenLine(), InternationalizationClass.getErrors().getString("E-SxA-07"), "E-SxA-07");
            }
            return null;
        }
    }

    /**
     * Metóda pre vysporiadanie sa z chybového stavu.
     *
     * <p> V rámci tejto metódy sa postupuje tak, že sa prechádzajú tokeny, až kým sa nenájde záchytný bod, pri tejto
     * metóde ide o pravú kučeravú zátvorku '}'.
     *
     * @return Err, ak sa program zotavil z chyby
     *         null, ak sa program nezotavil z chyby
     */
    private Node errorRecoveryB() {
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

    /**
     * Metóda pre vysporiadanie sa z chybového stavu.
     *
     * <p> V rámci tejto metódy sa postupuje tak, že sa prechádzajú tokeny, až kým sa nenájde záchytný bod, pri tejto
     * metóde ide o bodkočiarku ';'.
     *
     * @return Err, ak sa program zotavil z chyby
     *         null, ak sa program nezotavil z chyby
     */
    private Node errorRecoveryS() {
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

}