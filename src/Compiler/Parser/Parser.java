package Compiler.Parser;

import Compiler.Lexer.Scanner;
import Compiler.Lexer.Token;
import Compiler.Lexer.Tag;
import Compiler.SymbolTable.Kind;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import java.io.IOException;
import java.util.ArrayList;
import Compiler.AbstractSyntaxTree.*;
import com.sun.org.apache.xml.internal.security.Init;

public class Parser {
    private int position = 0;
    public ArrayList<Token> tokenStream = new ArrayList<>();
    private Node parseTree;
    private String type = "";

    public SymbolTable symbolTable = new SymbolTable(null);

    public Parser(String file) {
        Scanner scanner = new Scanner(file);
        Token tok;
        while (true) {
            try {
                tok = scanner.scan();
                if (tok.tag == Tag.EOF) {
                    break;
                } else {
                    tokenStream.add(tok);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printTree(Node parent, String indent) {
        parent.printData(indent);
        if (parent.getChilds() != null) {
            for (Node child: parent.getChilds()) {
                printTree(child, indent + "   ");
            }
        }
    }

    public void parse() {
        parseTree = translation_unit();
        if (parseTree == null) {
            System.out.println("Chyba v parse tree!");
        } else {
            printTree(parseTree, "");
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
            case Tag.LEFT_BRACES:
            case Tag.RIGHT_BRACES:
                System.out.println("Chybajúca zátvorka na riadku " + getTokenLine() + "!");
                break;
            case Tag.SEMICOLON:
                System.out.println("Chybajúca ; na riadku " + getTokenLine() + "!");
                break;
            case Tag.PLUS:
            case Tag.MINUS:
            case Tag.MULT:
            case Tag.DIV:
            case Tag.MOD:
                System.out.println("Chybajúci operátor na riadku " + getTokenLine() + "!");
                break;
            case Tag.IDENTIFIER:
                if (getTokenTag(position - 1) < 32) {
                    System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
                } else {
                    System.out.println("Chybajúci argument na riadku " + getTokenLine() + "!");
                }
                break;
            default:
                switch (getTokenTag(position - 1)) {
                    case Tag.LEFT_BRACKETS:
                    case Tag.RIGHT_BRACKETS:
                    case Tag.LEFT_BRACES:
                    case Tag.RIGHT_BRACES:
                        System.out.println("Zátvorka naviac na riadku " + getTokenLine() + "!");
                        break;
                    default:
                        if (tag < 32 && getTokenTag(position - 1) == Tag.IDENTIFIER) {
                            System.out.println("Chybné kľúčové slovo na riadku " + getTokenLine() + "!");
                        } else {
                            System.out.println("Chyba na riadku " + getTokenLine() + "!");
                        }
                }
                break;
        }
        return null;
    }

    private void clearEpsilon(Node child) {
        //vymazanie epsilonu
        int length = child.getChilds().size();
        for (int i = 0; i < length; i++) {
            if (child.getChilds().get(i).getTag() == -1) {
                child.getChilds().remove(i);
                break;
            }
        }
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
        int pos = position;
        Node child1;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                Record record = symbolTable.lookup(getTokenValue());
                if (record == null || record.getKind() != Kind.ENUMERATION_CONSTANT) {
                    nextToken();
                    return new Identifier(getTokenValue(position - 1));
                }
                break;
            case Tag.CHARACTER:
                nextToken();
                return new Constant("char", getTokenValue(position - 1));
            case Tag.STRING:
                nextToken();
                return new Constant("string", getTokenValue(position - 1));
            case Tag.LEFT_BRACKETS:
                nextToken();
                child1 = expression();
                Node child2 = null;
                if (child1 != null && !child1.isNone()) {
                    child2 = accept(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    return child1;
                }
                position = pos;
                return new None();
        }
        child1 = constant();
        if (!child1.isNone()) {
            return child1;
        }
        return None();
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
                return new Constant("enumeration_constant", getTokenValue(position - 1));
            case Tag.NUMBER:
                nextToken();
                //TODO: pridať zisťovanie typu
                return new Constant("1", getTokenValue(position - 1));
            case Tag.REAL:
                nextToken();
                //TODO: pridať zisťovanie typu
                return new Constant("2", getTokenValue(position - 1));
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
            symbolTable.insert(getTokenValue(), "", getTokenLine(), Kind.ENUMERATION_CONSTANT);

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
    //TODO: vyriešiť rest1 + pozrieť sa na odchytávanie chýb
    private Node postfix_expression() {
        int pos = position;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = type_name();
            Node child3 = null, child4 = null, child5;
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = accept(Tag.RIGHT_BRACKETS);
            }
            if (child2 != null) {
                child3 = accept(Tag.LEFT_BRACES);
            }
            if (child3 != null) {
                child4 = initializer_list();
            }
            Leaf term;
            if (child4 != null && !child4.getChilds().isEmpty()) {
                switch (getTokenTag()) {
                    case Tag.RIGHT_BRACES:
                        term = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                        nextToken();
                        child5 = rest1();
                        if (child5 != null && !child5.getChilds().isEmpty()) {
                            prod.addChilds(terminal);
                            prod.addChilds(child1);
                            prod.addChilds(child2);
                            prod.addChilds(child3);
                            prod.addChilds(child4);
                            prod.addChilds(term);
                            //vymazanie epsilonu
                            clearEpsilon(child5);
                            prod.getChilds().addAll(child5.getChilds());
                            return prod;
                        }
                        /*if (child5 != null && child5.getChilds().isEmpty()) {
                            System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                        }*/
                        break;
                    case Tag.COMMA:
                        term = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                        nextToken();
                        child5 = expect(Tag.RIGHT_BRACES);
                        Node child6 = null;
                        if (child5 != null) {
                            child6 = rest1();
                        }
                        if (child6 != null && !child6.getChilds().isEmpty()) {
                            prod.addChilds(terminal);
                            prod.addChilds(child1);
                            prod.addChilds(child2);
                            prod.addChilds(child3);
                            prod.addChilds(child4);
                            prod.addChilds(term);
                            prod.addChilds(child5);
                            //vymazanie epsilonu
                            clearEpsilon(child6);
                            prod.getChilds().addAll(child6.getChilds());
                            return prod;
                        }
                        /*if (child6 != null && child6.getChilds().isEmpty()) {
                            System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                        }*/
                        break;
                }
            }
            position = pos;
        }
        child1 = primary_expression();
        child2 = null;
        if (!child1.getChilds().isEmpty()) {
            child2 = rest1();
        }
        if (child2 != null && !child2.getChilds().isEmpty()) {
            prod.addChilds(child1);
            //vymazanie epsilonu
            clearEpsilon(child2);
            prod.getChilds().addAll(child2.getChilds());
            //prod.addChilds(child2);
            return prod;
        }
        return prod;
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
    private Node rest1() {
        Production prod = new Production("rest1");
        Leaf terminal;
        Node child1, child2 = null, child3 = null;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    child3 = rest1();
                }
                if (child3 != null && !child3.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
                if ((child1 != null && child1.getChilds().isEmpty()) ||
                        (child3 != null && child3.getChilds().isEmpty())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.DOT:
            case Tag.ARROW:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.IDENTIFIER);
                if (child1 != null) {
                    child2 = rest1();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    //vymazanie epsilonu
                    clearEpsilon(child2);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                if (child2 != null && child2.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.INC:
            case Tag.DEC:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest1();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    //vymazanie epsilonu
                    clearEpsilon(child1);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                if (getTokenTag() == Tag.RIGHT_BRACKETS) {
                    Leaf term = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                    nextToken();
                    child1 = rest1();
                    if (child1 != null && !child1.getChilds().isEmpty()) {
                        prod.addChilds(terminal);
                        prod.addChilds(term);
                        //vymazanie epsilonu
                        clearEpsilon(child1);
                        prod.getChilds().addAll(child1.getChilds());
                        return prod;
                    }
                    if (child1 != null && child1.getChilds().isEmpty()) {
                        System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    }
                    return null;
                }
                child1 = argument_expression_list();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    child3 = rest1();
                }
                if (child3 != null && !child3.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    return prod;
                }
                if ((child1 != null && child1.getChilds().isEmpty()) || (child3 != null && child3.getChilds().isEmpty())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
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
                    return null;
                }
            }
            return new ExpressionList(arr);
        }
        if (child1 != null && child1.isNone()) {
            System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
        }
        return null;
    }

    /**
     * unary_expression ->  postfix_expression
     *                    | '++' unary_expression
     *                    | '--' unary_expression
     *                    | unary_operator cast_expression
     *                    | SIZEOF '(' type_name ')'
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
                    return new UnaryOperator(child1, terminal);
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.SIZEOF:
                terminal = getTokenValue();
                nextToken();
                if (getTokenTag() == Tag.LEFT_BRACKETS) {
                    nextToken();
                    child1 = type_name();
                    child2 = null;
                    if (child1 != null && !child1.isNone()) {
                        child2 = expect(Tag.RIGHT_BRACKETS);
                    }
                    if (child2 != null) {
                        return new UnaryOperator(child1, terminal);
                    }
                    if (child1 != null && child1.isNone()) {
                        System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    }
                    return null;
                } else {
                    child1 = unary_expression();
                    if (child1 == null) {
                        return null;
                    }
                    if (!child1.isNone()) {
                        return new UnaryOperator(child1, terminal);
                    } else {
                        System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    }
                }
                return null;
        }
        String operator = unary_operator();
        if (!operator.equals("")) {
            child1 = cast_expression();
            if (child1.isNone()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                return null;
            } else {
                return new UnaryOperator(child1, operator);
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
        int pos = position;
        if (getTokenTag() == Tag.LEFT_BRACKETS) {
            nextToken();
            child1 = type_name();
            if (child1 != null && !child1.isNone()) {
                child2 = accept(Tag.RIGHT_BRACKETS);
            }
            if (child2 != null) {
                child3 = cast_expression();
            }
            if (child3 != null && !child3.isNone()) {
                return new Cast(child1, child3);
            }
            position = pos;
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    child1 = new BinaryOperator(binOperator, terminal, child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                    return new TernaryOperator(child1, child2, child4);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
                return null;
            } else {
                return new Assignment(child1, operator, child2);
            }
        }
        position = pos;
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
                    return null;
                }
            }
            return new ExpressionList(arr);
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
    //TODO: pozrieť sa na p_decl_body
    private ArrayList<Node> declaration() {
        Production prod = new Production("declaration");
        Node child1 = declaration_specifiers();
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
            prod.addChilds(new Leaf((byte) 254, "Error", -1));
            return prod;
        }
        if (!child1.getChilds().isEmpty()) {
            if (getTokenTag() == Tag.SEMICOLON) {
                prod.addChilds(child1);                                                       // declaration_specifiers
                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));     // ;
                nextToken();
                return prod;
            }
            child2 = init_declarator_list();
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
                prod.addChilds(new Leaf((byte) 254, "Error", -1));
                return prod;
            }
            if (!child2.getChilds().isEmpty()) {
                child3 = expect(Tag.SEMICOLON);
            }
            if (child3 != null) {
                prod.addChilds(child1);                                     // declaration_specifiers
                prod.addChilds(child2);                                     // init_declarator_list
                prod.addChilds(child3);                                     // ;
                return prod;
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
                prod.addChilds(new Leaf((byte) 254, "Error", -1));
                nextToken();
                return prod;
            } else {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                return null;
            }
        }
        return prod;
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
    //TODO: pozrieť sa na _add_declaration_specifiers + typy
    private Node declaration_specifiers() {
        Production prod = new Production("declaration_specifiers");
        Node child1 = storage_class_specifier();
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = declaration_specifiers();
            if (child2 == null) {
                return null;
            }
            if (!child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            } else {
                prod.addChilds(child1);
                return prod;
            }
        }
        child1 = type_specifier(true);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = declaration_specifiers();
            if (child2 == null) {
                return null;
            }
            if (!child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            } else {
                prod.addChilds(child1);
                return prod;
            }
        }
        child1 = type_qualifier(true);
        if (!child1.getChilds().isEmpty()) {
            child2 = declaration_specifiers();
            if (child2 == null) {
                return null;
            }
            if (!child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            } else {
                prod.addChilds(child1);
                return prod;
            }
        }
        return prod;
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
                    return null;
                }
            }
            return arr;
        }
        return new None();
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
        Node child1 = declarator(Kind.VARIABLE, "");
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
                }
                return null;
            } else {
                //TODO: zachytiť == ??
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
    //TODO: pozreiť sa na typy
    private Node storage_class_specifier() {
        Production prod = new Production("storage_class_specifier");
        switch (getTokenTag()) {
            case Tag.TYPEDEF:
            case Tag.EXTERN:
            case Tag.STATIC:
            case Tag.AUTO:
            case Tag.REGISTER:
                //zisťovanie typu identifikátoru
                type += getTokenValue() + " ";

                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
        }
        return prod;
    }

    /**
     * type_specifier ->  VOID | CHAR | SHORT | INT | LONG | FLOAT | DOUBLE | SIGNED | UNSIGNED
     *                  | struct_or_union_specifier
     *                  | enum_specifier
     *                  | TYPEDEF_NAME
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa na typy
    private Node type_specifier(boolean flag) {
        Production prod = new Production("type_specifier");
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
                //zisťovanie typu identifikátora
                if (flag) {
                    type += getTokenValue() + " ";
                }

                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
            case Tag.IDENTIFIER:
                //riešenie TYPEDEF_NAME
                Record record = symbolTable.lookup(getTokenValue());
                if (record != null) {
                    if (record.getKind() == Kind.TYPEDEF_NAME) {
                        //zisťovanie typu identifikátora
                        if (flag) {
                            type += getTokenValue() + " ";
                        }

                        prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                        nextToken();
                        return prod;
                    }
                }
                return prod;
        }
        Node child1 = struct_or_union_specifier(flag);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = enum_specifier(flag);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        return prod;
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
    private Node struct_or_union_specifier(boolean flag) {
        String child1 = struct_or_union(flag);
        ArrayList<Node> child2;
        Node child3 = null;
        if (!child1.equals("")) {
            switch (getTokenTag()) {
                case Tag.LEFT_BRACES:
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
                            return new Struct(null, child2);
                        } else {
                            return new Union(null, child2);
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
                    if (flag) {
                        //pridanie záznamu do symbolickej tabuľky
                        symbolTable.insert(getTokenValue(), type, getTokenLine());
                        type = "";
                    }
                    String terminal = getTokenValue();
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
                                return new Struct(terminal, child2);
                            } else {
                                return new Union(terminal, child2);
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
                            return new Struct(terminal, null);
                        } else {
                            return new Union(terminal, null);
                        }
                    }
            }
            if (getTokenTag() < 32) {
                System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
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
    private String struct_or_union(boolean flag) {
        switch (getTokenTag()) {
            case Tag.STRUCT:
            case Tag.UNION:
                //zisťovanie typu identifikátora
                if (flag) {
                    type += getTokenValue() + " ";
                }

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
        return new ArrayList<Node>();
    }

    /**
     * struct_declaration ->  specifier_qualifier_list ';'
     *                      | specifier_qualifier_list struct_declarator_list ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    // bolo upravené
    //TODO: pozrieť sa
    private ArrayList<Node> struct_declaration() {
        Production prod = new Production("struct_declaration");
        Node child1 = specifier_qualifier_list(true);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            if (getTokenTag() == Tag.SEMICOLON) {
                prod.addChilds(child1);                                                     // specifier_qualifier_list
                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));   // ;
                nextToken();
                return prod;
            }
            Node child2 = struct_declarator_list();
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
                prod.addChilds(new Leaf((byte) 254, "Error", -1));
                return prod;
            }
            if (!child2.getChilds().isEmpty()) {
                child3 = expect(Tag.SEMICOLON);
                if (child3 == null) {
                    // error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    if (getTokenTag() == Tag.SEMICOLON) {
                        nextToken();
                        prod.addChilds(new Leaf((byte) 254, "Error", -1));
                        return prod;
                    } else {
                        return null;
                    }
                } else {
                    prod.addChilds(child1);                         // specifier_qualifier_list
                    prod.addChilds(child2);                         // struct_declarator_list
                    prod.addChilds(child3);                         // ;
                    return prod;
                }
            }
        }
        return new ArrayList<Node>();
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
    //TODO: pozrieť sa na _add_declaration_specifier
    private Node specifier_qualifier_list(boolean flag) {
        Production prod = new Production("specifier_qualifier_ist");
        Node child1 = type_specifier(flag);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = specifier_qualifier_list(flag);
            if (child2 == null) {
                return null;
            }
            if (!child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            } else {
                prod.addChilds(child1);
                return prod;
            }
        }
        child1 = type_qualifier(flag);
        if (!child1.getChilds().isEmpty()) {
            child2 = specifier_qualifier_list(flag);
            if (child2 == null) {
                return null;
            }
            if (!child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            } else {
                prod.addChilds(child1);
                return prod;
            }
        }
        return prod;
    }

    /**
     * struct_declarator_list ->  struct_declarator_list ',' struct_declarator
     *                          | struct_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa
    private ArrayList<Node> struct_declarator_list() {
        Production prod = new Production("struct_declarator_list");
        Node child1 = struct_declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            while (getTokenTag() == Tag.COMMA) {
                Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = struct_declarator();
                if (child1 == null) {
                    return null;
                }
                if (!child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    return null;
                }
            }
            return prod;
        }
        return new ArrayList<Node>();
    }

    /**
     * struct_declarator ->  ':' constant_expression
     *                     | declarator ':' constant_expression
     *                     | declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa
    private Node struct_declarator() {
        Production prod = new Production("struct_declarator");
        Node child1;
        if (getTokenTag() == Tag.COLON) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = constant_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                return prod;
            }
            if (child1 != null && child1.getChilds().isEmpty()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
            }
            return null;
        }
        child1 = declarator(Kind.VARIABLE, "");
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            if (getTokenTag() == Tag.COLON) {
                Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                Node child2 = constant_expression();
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(child1);                             // declarator
                    prod.addChilds(terminal);                           // :
                    prod.addChilds(child2);                             // constant_expression
                    return prod;
                }
                if (child2 != null && child2.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            } else {
                prod.addChilds(child1);
                return prod;
            }
        }
        return prod;
    }

    /**
     * enum_specifier -> ENUM left13
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node enum_specifier(boolean flag) {
        if (getTokenTag() == Tag.ENUM) {
            //zisťovanie typu identifikátora
            if (flag) {
                type += getTokenValue() + " ";
            }

            nextToken();
            Node child1 = left13(flag);
            if (child1 != null && !child1.getChilds().isEmpty()) {
                return child1;
            }
            if (child1 != null && child1.getChilds().isEmpty()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
    private Node left13(boolean flag) {
        Node child1, child2, child3;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACES:
                nextToken();
                child1 = enumerator_list();
                if (child1 != null && !child1.isNone()) {
                    switch (getTokenTag()) {
                        case Tag.RIGHT_BRACES:
                            nextToken();
                            return new Enum(null, child1);
                        case Tag.COMMA:
                            nextToken();
                            child2 = expect(Tag.RIGHT_BRACES);
                            if (child2 != null) {
                                return new Enum(null, child1);
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
                if (flag) {
                    //pridanie záznamu do symbolickej tabuľky
                    symbolTable.insert(getTokenValue(), type, getTokenLine());
                    type = "";
                }

                String terminal = getTokenValue();
                nextToken();
                if (getTokenTag() == Tag.LEFT_BRACES) {
                    expect(Tag.LEFT_BRACES);
                    child1 = enumerator_list();
                    if (child1 != null && !child1.isNone()) {
                        switch (getTokenTag()) {
                            case Tag.RIGHT_BRACES:
                                nextToken();
                                return new Enum(terminal, child1);
                            case Tag.COMMA:
                                nextToken();
                                child3 = expect(Tag.RIGHT_BRACES);
                                if (child3 != null) {
                                    return new Enum(terminal, child1);
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
                    return new Enum(terminal, null);
                }
        }
        if (getTokenTag() < 32) {
            System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
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
                    return null;
                }
            }
            return new EnumeratorList(arr);
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
        if (!child1.equals("")) {
            if (getTokenValue().equals("=")) {
                nextToken();
                Node child2 = constant_expression();
                if (child2 != null && !child2.isNone()) {
                    return new Enumerator(child1, child2);
                }
                if (child2 != null && child2.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            } else {
                return new Enumerator(child1, null);
            }
        }
        return new None();
    }

    /**
     * type_qualifier -> CONST | VOLATILE
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //TODO: pozrieť sa na typy
    private Node type_qualifier(boolean flag) {
        Production prod = new Production("type_qualifier");
        switch (getTokenTag()) {
            case Tag.CONST:
            case Tag.VOLATILE:
                //zisťovanie typu identifikátora
                if (flag) {
                    type += getTokenValue() + " ";
                }

                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
        }
        return prod;
    }

    /**
     * declarator ->  pointer direct_declarator
     *              | direct_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa
    private Node declarator(byte kind, String id) {
        Production prod = new Production("declarator");
        Node child1 = pointer(true);
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = direct_declarator(kind, id);
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
            }
            return prod;
        }
        child1 = direct_declarator(kind, id);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        return prod;
    }

    /**
     * direct_declarator ->  IDENTIFIER rest18
     *                     | '(' declarator ')' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //TODO: pozrieť sa
    private Node direct_declarator(byte kind, String id) {
        Production prod = new Production("direct_declarator");
        Leaf terminal;
        Node child1, child2 = null, child3 = null;
        int pos = position;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                //pridanie identifikátoru do
                if(getTokenTag(position + 1) == Tag.LEFT_PARENTHESES) {
                    //ide o pole
                    if (kind == Kind.PARAMETER) {
                        //ide o parameter funkcie
                        symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.ARRAY_PARAMETER);
                        //pridanie parametru k funkcii
                        Record record = symbolTable.lookup(id);
                        record.getParameters().add(getTokenValue());
                        symbolTable.setValue(id, record);
                    } else {
                        if (getTokenTag(position + 2) == Tag.NUMBER) {
                            //viem veľkosť poľa
                            symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.ARRAY, Integer.parseInt(getTokenValue(position + 2)));
                        } else {
                            symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.ARRAY);
                        }
                    }
                } else if (getTokenTag(position + 1) == Tag.LEFT_BRACKETS) {
                    //ide o funkciu
                    symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.FUNCTION);
                } else {
                    if (kind == Kind.PARAMETER) {
                        symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.PARAMETER);
                        //pridanie parametru k funkcii
                        Record record = symbolTable.lookup(id);
                        record.getParameters().add(getTokenValue());
                        symbolTable.setValue(id, record);
                    } else {
                        symbolTable.insert(getTokenValue(), type, getTokenLine());
                    }
                }
                type = "";

                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest18(terminal.getValue());
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    //vymazanie epsilonu
                    clearEpsilon(child1);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = declarator(kind, "");
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = accept(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    child3 = rest18("");
                }
                if (child3 != null && !child3.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
                position = pos;
                return prod;
        }
        //TODO: nie som si istý
        if (getTokenTag() < 32) {
            System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
            return null;
        }
        return prod;
    }

    /**
     * rest18 ->  '[' left16
     *          | '(' left17
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest18(String id) {
        Production prod = new Production("rest18");
        Leaf terminal;
        Node child1;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left16();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left17(id);
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
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
    private Node left16() {
        Production prod = new Production("left16");
        int pos = position;
        Node child1, child2;
        Leaf terminal;
        switch (getTokenTag()) {
            case Tag.MULT:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = accept(Tag.RIGHT_PARENTHESES);
                if (child1 != null) {
                    child2 = rest18("");
                    if (child2 == null) {
                        return null;
                    } else {
                        prod.addChilds(terminal);
                        prod.addChilds(child1);
                        //vymazanie epsilonu
                        clearEpsilon(child2);
                        prod.getChilds().addAll(child2.getChilds());
                        //prod.addChilds(child2);
                        return prod;
                    }
                } else {
                    position = pos;
                    break;
                }
            case Tag.STATIC:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left18();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.RIGHT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest18("");
                if (child1 != null) {
                    prod.addChilds(terminal);
                    //vymazanie epsilonu
                    clearEpsilon(child1);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
        }
        Node child3;
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest18("");
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = type_qualifier_list(false);
        if (!child1.getChilds().isEmpty()) {
            child2 = left19();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        return prod;
    }

    /**
     * left17 ->  parameter_type_list ')' rest18
     *          | ')' rest18
     *          | identifier_list rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left17(String id) {
        Production prod = new Production("left17");
        Node child1;
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = rest18("");
            if (child1 != null) {
                prod.addChilds(terminal);
                //vymazanie epsilonu
                clearEpsilon(child1);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        child1 = parameter_type_list(id);
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest18("");
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = identifier_list();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest18("");
            if (child2 == null) {
                return null;
            } else {
                prod.addChilds(child1);
                //vymazanie epsilonu
                clearEpsilon(child2);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        return prod;
    }

    /**
     * left18 ->  type_qualifier_list assignment_expression ']' rest18
     *          | assignment_expression ']' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left18() {
        Production prod = new Production("left18");
        Node child1 = type_qualifier_list(false);
        Node child2, child3, child4;
        if (!child1.getChilds().isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null) {
                return null;
            }
            if (child2.getChilds().isEmpty()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                return null;
            } else {
                child3 = expect(Tag.RIGHT_PARENTHESES);
                if (child3 == null) {
                    return null;
                } else {
                    child4 = rest18("");
                    if (child4 == null) {
                        return null;
                    } else {
                        prod.addChilds(child1);
                        prod.addChilds(child2);
                        prod.addChilds(child3);
                        //vymazanie epsilonu
                        clearEpsilon(child4);
                        prod.getChilds().addAll(child4.getChilds());
                        //prod.addChilds(child4);
                        return prod;
                    }
                }
            }
        }
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest18("");
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
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
    private Node left19() {
        Production prod = new Production("left19");
        int pos = position;
        Node child1, child2 = null, child3 = null;
        Leaf terminal;
        switch (getTokenTag()) {
            case Tag.MULT:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = accept(Tag.RIGHT_PARENTHESES);
                if (child1 != null) {
                    child2 = rest18("");
                    if (child2 == null) {
                        return null;
                    } else {
                        prod.addChilds(terminal);
                        prod.addChilds(child1);
                        //vymazanie epsilonu
                        clearEpsilon(child2);
                        prod.getChilds().addAll(child2.getChilds());
                        //prod.addChilds(child2);
                        return prod;
                    }
                } else {
                    position = pos;
                    break;
                }
            case Tag.STATIC:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = assignment_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    child3 = rest18("");
                }
                if (child3 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.RIGHT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest18("");
                if (child1 != null) {
                    prod.addChilds(terminal);
                    //vymazanie epsilonu
                    clearEpsilon(child1);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
        }
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest18("");
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
    }

    /**
     * pointer ->  '*' type_qualifier_list pointer
     *           | '*' type_qualifier_list
     *           | '*' pointer
     *           | '*'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //TODO: pozrieť
    private Node pointer(boolean flag) {
        Production prod = new Production("pointer");
        if (getTokenTag() == Tag.MULT) {
            //zisťovanie typu identifikátora
            if (flag) {
                type += getTokenValue() + " ";
            }

            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = type_qualifier_list(flag);
            Node child2;
            if (!child1.getChilds().isEmpty()) {
                child2 = pointer(flag);
                if (!child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                } else {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                }
                return prod;
            }
            child1 = pointer(flag);
            if (!child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
            } else {
                prod.addChilds(terminal);
            }
            return prod;
        }
        return prod;
    }

    /**
     * type_qualifier_list ->  type_qualifier_list type_qualifier
     *                       | type_qualifier
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa
    private Node type_qualifier_list(boolean flag) {
        Production prod = new Production("type_qualifier_list");
        Node child1 = type_qualifier(flag);
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            child1 = type_qualifier(flag);
            while (!child1.getChilds().isEmpty()) {
                prod.addChilds(child1);
                child1 = type_qualifier(flag);
            }
            return prod;
        }
        return prod;
    }

    /**
     * parameter_type_list ->  parameter_list ',' '...'
     *                       | parameter_list
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node parameter_type_list(String id) {
        ParameterList child1 = parameter_list(id);
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
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
    private ParameterList parameter_list(String id) {
        Node child1 = parameter_declaration(id);
        if (child1 == null) {
            return null;
        }
        ArrayList<Node> arr = new ArrayList<>();
        if (!child1.isNone()) {
            arr.add(child1);
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                child1 = parameter_declaration(id);
                if (child1 == null) {
                    return null;
                }
                if (!child1.isNone()) {
                    arr.add(child1);
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    return null;
                }
            }
            return new ParameterList(arr);
        }
        return new None();
    }

    /**
     * parameter_declaration -> declaration_specifiers left22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa na typy
    private Node parameter_declaration(String id) {
        Production prod = new Production("parameter_declaration");
        Node child1 = declaration_specifiers();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left22(id);
            if (child2 == null) {
                return null;
            }
            if (child2.getChilds().isEmpty()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                return null;
            } else {
                prod.addChilds(child1);
                //vymazanie epsilonu
                clearEpsilon(child2);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        return prod;
    }

    /**
     * left22 ->  declarator
     *          | abstract_declarator
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left22(String id) {
        Production prod = new Production("left22");
        Node child1 = declarator(Kind.PARAMETER, id);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = abstract_declarator();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
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
            arr.add(new Identifier(getTokenValue()));
            nextToken();
            while (getTokenTag() == Tag.COMMA) {
                nextToken();
                if (getTokenTag() == Tag.IDENTIFIER) {
                    arr.add(new Identifier(getTokenValue()));
                    nextToken();
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    return null;
                }
            }
            return new ParameterList(arr);
        }
        if (getTokenTag() < 32) {
            System.out.println("Využitie kľúčového slova namiesto premennej na riadku " + getTokenLine() + "!");
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
    //TODO: pozrieť sa _fix_decl_name_type
    private Node type_name() {
        Production prod = new Production("type_name");
        Node child1 = specifier_qualifier_list(false);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = abstract_declarator();
            if (child2 == null) {
                return null;
            }
            if (!child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
            } else {
                prod.addChilds(child1);
            }
            return prod;
        }
        return prod;
    }

    /**
     * abstract_declarator ->  pointer direct_abstract_declarator
     *                       | pointer
     *                       | direct_abstract_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa _type_modify_decl
    private Node abstract_declarator() {
        Production prod = new Production("abstractor_declarator");
        Node child1 = pointer(false);
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = direct_abstract_declarator();
            if (child2 == null) {
                return null;
            }
            if (!child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
            } else {
                prod.addChilds(child1);
            }
            return prod;
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
    //TODO: pozrieť sa
    private Node direct_abstract_declarator() {
        Production prod = new Production("direct_abstract_declarator");
        Leaf terminal;
        Node child1;
        int pos = position;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left25();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                position = pos;
                return prod;
            case Tag.LEFT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left26();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
        }
        return prod;
    }

    /**
     * left25 ->  abstract_declarator ')' rest22
     *          | ')' rest22
     *          | parameter_type_list ')' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left25() {
        Production prod = new Production("left25");
        Node child1;
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = rest22();
            if (child1 != null) {
                prod.addChilds(terminal);
                //vymazanie epsilonu
                clearEpsilon(child1);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        child1 = abstract_declarator();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22();
                if (child3 == null) {
                    return null;
                }  else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = parameter_type_list("");
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22();
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
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
    private Node left26() {
        Production prod = new Production("left26");
        int pos = position;
        Leaf terminal;
        Node child1, child2;
        switch (getTokenTag()) {
            case Tag.RIGHT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest22();
                if (child1 != null) {
                    prod.addChilds(terminal);
                    //vymazanie epsilonu
                    clearEpsilon(child1);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
            case Tag.MULT:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = accept(Tag.RIGHT_PARENTHESES);
                if (child1 != null) {
                    child2 = rest22();
                    if (child2 == null) {
                        return null;
                    } else {
                        prod.addChilds(terminal);
                        prod.addChilds(child1);
                        //vymazanie epsilonu
                        clearEpsilon(child2);
                        prod.getChilds().addAll(child2.getChilds());
                        //prod.addChilds(child2);
                        return prod;
                    }
                } else {
                    position = pos;
                    break;
                }
            case Tag.STATIC:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left27();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
        }
        child1 = assignment_expression();
        Node child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22();
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = type_qualifier_list(false);
        if (!child1.getChilds().isEmpty()) {
            child2 = left28();
            if (child2 == null) {
                return null;
            }
            if (child2.getChilds().isEmpty()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        return prod;
    }

    /**
     * left27 ->  type_qualifier_list assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left27() {
        Production prod = new Production("left27");
        Node child1 = type_qualifier_list(false);
        Node child2, child3, child4;
        if (!child1.getChilds().isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null) {
                return null;
            }
            if (child2.getChilds().isEmpty()) {
                System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                return null;
            } else {
                child3 = expect(Tag.RIGHT_PARENTHESES);
                if (child3 == null) {
                    return null;
                } else {
                    child4 = rest22();
                    if (child4 == null) {
                        return null;
                    } else {
                        prod.addChilds(child1);
                        prod.addChilds(child2);
                        prod.addChilds(child3);
                        //vymazanie epsilonu
                        clearEpsilon(child4);
                        prod.getChilds().addAll(child4.getChilds());
                        //prod.addChilds(child4);
                        return prod;
                    }
                }
            }
        }
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22();
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
    }

    /**
     * left28 ->  STATIC assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     *          | ']' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left28() {
        Production prod = new Production("left28");
        Leaf terminal;
        Node child1, child2 = null, child3 = null;
        switch (getTokenTag()) {
            case Tag.STATIC:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = assignment_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    child3 = rest22();
                }
                if (child3 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.RIGHT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest22();
                if (child1 != null) {
                    prod.addChilds(terminal);
                    //vymazanie epsilonu
                    clearEpsilon(child1);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
        }
        child1 = assignment_expression();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_PARENTHESES);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22();
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
    }

    /**
     * rest22 ->  '[' left26
     *          | '(' left29
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest22() {
        Production prod = new Production("rest22");
        Leaf terminal;
        Node child1;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left26();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left29();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                if (child1 != null && child1.getChilds().isEmpty()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
        }
    }

    /**
     * left29 ->  ')' rest22
     *          | parameter_type_list ')' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left29() {
        Production prod = new Production("left29");
        Node child1;
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = rest22();
            if (child1 != null) {
                prod.addChilds(terminal);
                //vymazanie epsilonu
                clearEpsilon(child1);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        child1 = parameter_type_list("");
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22();
                if (child3 == null) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    //vymazanie epsilonu
                    clearEpsilon(child3);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
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
                return null;
            } else {
                NamedInitializer nam = new NamedInitializer(child1, child2);
                ArrayList<Node> arr = new ArrayList<>();
                arr.add(nam);
                InitializationList init = new InitializationList(arr);
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
            InitializationList init = new InitializationList(arr);
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
            return null;
        }
        return EmptyStatement();
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
        if (!child1.getChilds().isEmpty()) {
            arr.add(child1);
            child1 = designator();
            if (child1 == null) {
                return null;
            }
            while (!child1.getChilds().isEmpty()) {
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
            case Tag.LEFT_PARENTHESES:
                nextToken();
                child1 = constant_expression();
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    return child1;
                }
                if (child1 != null && child1.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.DOT:
                nextToken();
                child1 = expect(Tag.IDENTIFIER);
                if (child1 != null) {
                    return new Identifier(getTokenValue(position - 1));
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
    private Node statement() {
        int pos = position;
        Node child1 = labeled_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.isNone()) {
            return child1;
        }
        position = pos;
        child1 = compound_statement();
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
        int pos = position;
        Node child1, child2 = null, child3 = null;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                String terminal = getTokenValue();
                nextToken();
                child1 = accept(Tag.COLON);
                if (child1 != null) {
                    child2 = statement();
                }
                if (child2 != null && !child2.isNone()) {
                    return new Label(terminal, child2);
                }
                position = pos;
                break;
            case Tag.DEFAULT:
                nextToken();
                child1 = expect(Tag.COLON);
                if (child1 != null) {
                    child2 = statement();
                }
                if (child2 != null && !child2.isNone()) {
                    return new Default(child2);
                }
                if (child2 != null && child2.isNone()) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.CASE:
                nextToken();
                child1 = constant_expression();
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.COLON);
                }
                if (child2 != null) {
                    child3 = statement();
                }
                if (child3 != null && !child3.isNone()) {
                    return new Case(child1, child3);
                }
                if ((child1 != null && child1.isNone()) || (child3 != null && child3.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
    private Node compound_statement() {
        if (getTokenTag() == Tag.LEFT_BRACES) {
            nextToken();
            if (getTokenTag() == Tag.RIGHT_BRACES) {
                nextToken();
                return new Compound(null);
            }
            SymbolTable parent = symbolTable;
            //vytvorenie vnorenej tabuľky
            symbolTable = new SymbolTable(symbolTable);
            if (parent != null) {
                parent.addChild(symbolTable);
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
                    symbolTable = parent;
                    return new Compound(child1);
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
        if (child1 != null && !child1.isEmpty()) {
            arr.addAll(child1);
            child1 = block_item();
            if (child1 == null) {
                return null;
            }
            while (!child1.isNone()) {
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
        Node child2 = statement();
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
        switch (getTokenTag()) {
            case Tag.IF:
                nextToken();
                child1 = expect(Tag.LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.isNone()) {
                    child3 = expect(Tag. RIGHT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = statement();
                }
                if (child4 != null && !child4.isNone()) {
                    if (getTokenTag() == Tag.ELSE) {
                        nextToken();
                        child5 = statement();
                        if (child5 != null && !child5.isNone()) {
                            return new If(child2, child4, child5);
                        }
                    } else {
                        return new If(child2, child4, null);
                    }
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())
                        || (child5 != null && child5.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.SWITCH:
                nextToken();
                child1 = expect(Tag.LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.isNone()) {
                    child3 = expect(Tag. RIGHT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = statement();
                }
                if (child4 != null && !child4.isNone()) {
                    return new Switch(child2, child4);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
        switch (getTokenTag()) {
            case Tag.WHILE:
                nextToken();
                child1 = expect(Tag. LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.isNone()) {
                    child3 = expect(Tag. RIGHT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = statement();
                }
                if (child4 != null && !child4.isNone()) {
                    return new While(child2, child4);
                }
                if ((child2 != null && child2.isNone()) || (child4 != null && child4.isNone())) {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                }
                return null;
            case Tag.DO:
                nextToken();
                child1 = statement();
                if (child1 != null && !child1.isNone()) {
                    child2 = expect(Tag.WHILE);
                }
                if (child2 != null) {
                    child3 = expect(Tag. LEFT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = expression();
                }
                if (child4 != null && !child4.isNone()) {
                    child5 = expect(Tag. RIGHT_BRACKETS);
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
                    return new DoWhile(child4, child1);
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
                nextToken();
                child1 = expect(Tag. LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = left33();
                }
                if (child2 == null) {
                    return null;
                }
                if (!child2.isNone()) {
                    return child2;
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
    private Node left33() {
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
                return null;
            } else {
                 if (getTokenTag() == Tag.RIGHT_BRACKETS) {
                     nextToken();
                     child3 = statement();
                     if (child3 != null && !child3.isNone()) {
                         if (child1.isEmpty()) {
                             child1 = null;
                         }
                         if (child2.isEmpty()) {
                             child2 = null;
                         }
                         return new For(child1, child2, null, child3);
                     }
                     if (child3 != null && child3.isNone()) {
                         System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                     }
                     return null;
                 }
                 child3 = expression();
                 Node child4, child5;
                 if (child3 == null) {
                     return null;
                 }
                 if (!child3.isNone()) {
                     child4 = expect(Tag.RIGHT_BRACKETS);
                     if (child4 == null) {
                         return null;
                     } else {
                         child5 = statement();
                         if (child5 == null) {
                             return null;
                         }
                         if (child5.isNone()) {
                             System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                             return null;
                         } else {
                             if (child1.isEmpty()) {
                                 child1 = null;
                             }
                             if (child2.isEmpty) {
                                 child2 = null;
                             }
                             return new For(child1, child2, child3, child5);
                         }
                     }
                 } else {
                     System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                     return null;
                 }
            }
        }
        child1 = declaration();
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
                return null;
            } else {
                if (getTokenTag() == Tag.RIGHT_BRACKETS) {
                    nextToken();
                    child3 = statement();
                    if (child3 != null && !child3.isNone()) {
                        if (child2.isEmpty()) {
                            child2 = null;
                        }
                        return new For(child1, child2, null, child3);
                    }
                    if (child3 != null && child3.isNone()) {
                        System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                    }
                    return null;
                }
                child3 = expression();
                Node child4, child5;
                if (child3 == null) {
                    return null;
                }
                if (!child3.isNone()) {
                    child4 = expect(Tag.RIGHT_BRACKETS);
                    if (child4 == null) {
                        return null;
                    } else {
                        child5 = statement();
                        if (child5 == null) {
                            return null;
                        }
                        if (child5.isNone()) {
                            System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
                            return null;
                        } else {
                            if (child2.isEmpty()) {
                                child2 = null;
                            }
                            return new For(child1, child2, child3, child5);
                        }
                    }
                } else {
                    System.out.println("Syntaktická chyba na riadku " + getTokenLine() + "!");
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
        switch (getTokenTag()) {
            case Tag.GOTO:
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
                    return new Goto(terminal);
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
                nextToken();
                child1 = expect(Tag.SEMICOLON);
                if (child1 != null) {
                    return new Continue();
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
                nextToken();
                child1 = expect(Tag.SEMICOLON);
                if (child1 != null) {
                    return new Break();
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
                nextToken();
                if (getTokenTag() == Tag.SEMICOLON) {
                    nextToken();
                    return new Return(null);
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
                        return new Return(child1);
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
    private Node translation_unit() {
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
            return child1;
        }
        return new None();
    }

    /**
     * external_declaration ->  function_definition
     *                        | declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //DONE
    private ArrayList<Node> external_declaration() {
        int pos = position;
        Node child1 = function_definition();
        if (!child1.isNone()) {
            ArrayList<Node> arr = new ArrayList<>();
            arr.add(child1);
            return arr;
        }
        position = pos;
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
    //TODO: pozrieť sa
    private Node function_definition() {
        Production prod = new Production("functional_definition");
        Node child1 = declaration_specifiers();
        Node child2 = null, child3;
        if(child1 != null && !child1.getChilds().isEmpty()) {
            child2 = declarator(Kind.FUNCTION, "");
        }
        if (child2 != null && !child2.getChilds().isEmpty()) {
            child3 = declaration_list();
            Node child4 = null;
            if (child3 != null && !child3.getChilds().isEmpty()) {
                child4 = compound_statement();
            }
            if (child4 != null && !child4.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
                prod.addChilds(child3);
                prod.addChilds(child4);
                return prod;
            }
            child3 = compound_statement();
            if (child3 != null && !child3.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
                prod.addChilds(child3);
                return prod;
            }
        }
        return prod;
    }

    /**
     * declaration_list ->  declaration_list declaration
     *                    | declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //DONE
    private Node declaration_list() {
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
}