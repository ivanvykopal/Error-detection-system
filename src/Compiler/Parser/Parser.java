package Compiler.Parser;

import Compiler.Lexer.Scanner;
import Compiler.Lexer.Token;
import Compiler.Lexer.Tag;
import Compiler.SymbolTable.Kind;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private int position = 0;
    private ArrayList<Token> tokenStream = new ArrayList<>();
    private Production parseTree;
    private String type = "";

    public SymbolTable symbolTable = null;

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

    private void nextToken() {
        position++;
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
                System.out.println("Chybajúci argument na riadku " + getTokenLine() + "!");
                break;
            default:
                System.out.println("Chyba na riadku " + getTokenLine() + "!");
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
    private Node primary_expression() {
        Production prod = new Production("primary_expression");
        int pos = position;
        Node child1;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
            case Tag.CHARACTER:
            case Tag.STRING:
                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
            case Tag.LEFT_BRACKETS:
                Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expression();
                Node child2 = null;
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = accept(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    return prod;
                }
                position = pos;
                return prod;
            default:
                child1 = constant();
                if (!child1.getChilds().isEmpty()) {
                    prod.addChilds(child1);
                }
                return prod;
        }
    }

    /**
     * constant ->  NUMBER
     *            | REAL
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node constant() {
        Production prod = new Production("constant");
        switch (getTokenTag()) {
            case Tag.NUMBER:
            case Tag.REAL:
                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
            default:
                return prod;
        }
    }

    /**
     * enumeration_constant -> IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node enumeration_constant() {
        Production prod = new Production("enumeration_constant");
        Node child = accept(Tag.IDENTIFIER);
        if (child != null) {
            prod.addChilds(child);
        }
        return prod;
    }

    /**
     * postfix_expression ->  primary_expression rest1
     *                      | '(' type_name ')' '{' initializer_list left1
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node postfix_expression() {
        Production prod = new Production("postfix_expression");
        int pos = position;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = type_name();
            Node child3 = null, child4 = null, child5 = null;
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = accept(Tag.RIGHT_BRACKETS);
            }
            if (child2 != null) {
                child3 = accept(Tag.LEFT_BRACES);
            }
            if (child3 != null) {
                child4 = initializer_list();
            }
            if (child4 != null && !child4.getChilds().isEmpty()) {
                child5 = left1();
            }
            if (child5 != null && !child5.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.addChilds(child2);
                prod.addChilds(child3);
                prod.addChilds(child4);
                prod.getChilds().addAll(child5.getChilds());
                //prod.addChilds(child5);
                return prod;
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
     *         | '(' left2
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
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
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
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
            case Tag.INC:
            case Tag.DEC:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest1();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left2();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
        }
    }

    /**
     * left1 ->  '}' rest1
     *         | ',' '}' rest1
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left1() {
        Production prod = new Production("left1");
        Leaf terminal;
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.RIGHT_BRACES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest1();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
            case Tag.COMMA:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.RIGHT_BRACES);
                if (child1 != null) {
                    child2 = rest1();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
            default:
                return prod;
        }
    }

    /**
     * left2 ->  ')' rest1
     *         | argument_expression_list ')' rest1
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left2() {
        Production prod = new Production("left2");
        Leaf terminal;
        Node child1, child2 = null, child3 = null;
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = rest1();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
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
            prod.addChilds(child1);
            prod.addChilds(child2);
            prod.getChilds().addAll(child3.getChilds());
            //prod.addChilds(child3);
            return prod;
        }
        return null;
    }

    /**
     * argument_expression_list -> assignment_expression rest2
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node argument_expression_list() {
        Production prod = new Production("argument_expression_list");
        Node child1 = assignment_expression();
        Node child2 = null;
        if (child1 != null && !child1.getChilds().isEmpty()) {
            child2 = rest2();
        }
        if (child2 != null && !child2.getChilds().isEmpty()) {
            prod.addChilds(child1);
            prod.getChilds().addAll(child2.getChilds());
            //prod.addChilds(child2);
            return prod;
        }
        return null;
    }

    /**
     * rest2 ->  ',' assignment_expression rest2
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest2() {
        Production prod = new Production("rest2");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.COMMA) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = assignment_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest2();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * unary_expression ->  postfix_expression
     *                    | '++' unary_expression
     *                    | '--' unary_expression
     *                    | unary_operator cast_expression
     *                    | SIZEOF left3
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node unary_expression() {
        Production prod = new Production("unary_expression");
        Leaf terminal;
        Node child1, child2;
        switch (getTokenTag()) {
            case Tag.INC:
            case Tag.DEC:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = unary_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    return prod;
                }
                return null;
            case Tag.SIZEOF:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left3();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
        }
        child1 = unary_operator();
        if (!child1.getChilds().isEmpty()) {
            child2 = cast_expression();
            if (child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            }
        }
        child1 = postfix_expression();
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        return prod;
    }

    /**
     *  left3 ->  unary_expression
     *          | '(' type_name ')'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left3() {
        Production prod = new Production("left3");
        Node child1 = unary_expression();
        Node child2 = null;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        if (getTokenTag() == Tag.LEFT_BRACKETS) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = type_name();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = expect(Tag.RIGHT_BRACKETS);
            }
            if (child2 != null) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            }
            return null;
        }
        return prod;
    }

    /**
     * unary_operator -> '&' | '*' | '+' | '-' | '~' | '!'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node unary_operator() {
        Production prod = new Production("unary_operator");
        switch (getTokenTag()) {
            case Tag.AND:
            case Tag.MULT:
            case Tag.PLUS:
            case Tag.MINUS:
            case Tag.BITWISE_NOT:
            case Tag.LOGICAL_NOT:
                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
            default:
                return prod;
        }
    }

    /**
     * cast_expression ->  unary_expression
     *                   | '(' type_name ')' cast_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node cast_expression() {
        Production prod = new Production("cast_expression");
        Leaf terminal;
        Node child1, child2 = null, child3 = null;
        int pos = position;
        if (getTokenTag() == Tag.LEFT_BRACKETS) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = type_name();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = accept(Tag.RIGHT_BRACKETS);
            }
            if (child2 != null) {
                child3 = cast_expression();
            }
            if (child3 != null && !child3.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.addChilds(child2);
                prod.addChilds(child3);
                return prod;
            }
            position = pos;
        }
        child1 = unary_expression();
        if (child1 != null && !child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        return prod;
    }

    /**
     * multiplicative_expression -> cast_expression rest3
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node multiplicative_expression() {
        Production prod = new Production("multiplicative_expression");
        Node child1 = cast_expression();
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = rest3();
            if (child2 == null) {
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
     * rest3 ->  '*' cast_expression rest3
     *         | '/' cast_expression rest3
     *         | '%' cast_expression rest3
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node rest3() {
        Production prod = new Production("rest3");
        Leaf terminal;
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.MULT:
            case Tag.DIV:
            case Tag.MOD:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = cast_expression();
                if (!child1.getChilds().isEmpty()) {
                    child2 = rest3();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
        }
    }

    /**
     * additive_expression -> multiplicative_expression rest4
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node additive_expression() {
        Production prod = new Production("additive_expression");
        Node child1 = multiplicative_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest4();
            if (child2 == null) {
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
     * rest4 ->  '+' multiplicative_expression rest4
     *         | '-' multiplicative_expression rest4
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest4() {
        Production prod = new Production("rest4");
        Leaf terminal;
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.PLUS:
            case Tag.MINUS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = multiplicative_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = rest4();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
        }
    }

    /**
     * shift_expression -> additive_expression rest5
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node shift_expression() {
        Production prod = new Production("shift_expression");
        Node child1 = additive_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest5();
            if (child2 == null) {
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
     * rest5 ->  '<<' additive_expression rest5
     *         | '>>' additive_expression rest5
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest5() {
        Production prod = new Production("rest5");
        Leaf terminal;
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.LEFT_SHIFT:
            case Tag.RIGHT_SHIFT:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = additive_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = rest5();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
        }
    }

    /**
     * relational_expression -> shift_expression rest6
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node relational_expression() {
        Production prod = new Production("relational_expression");
        Node child1 = shift_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest6();
            if (child2 == null) {
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
     * rest6 ->  '<' shift_expression rest6
     *         | '>' shift_expression rest6
     *         | '<=' shift_expression rest6
     *         | '>=' shift_expression rest6
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest6() {
        Production prod = new Production("rest6");
        Leaf terminal;
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.LT:
            case Tag.GT:
            case Tag.LEQT:
            case Tag.GEQT:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = shift_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = rest6();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
        }
    }

    /**
     * equality_expression -> relational_expression rest7
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node equality_expression() {
        Production prod = new Production("equality_expression");
        Node child1 = relational_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest7();
            if (child2 == null) {
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
     * rest7 ->  '==' relational_expression rest7
     *         | '!=' relational_expression rest7
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest7() {
        Production prod = new Production("rest7");
        Leaf terminal;
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.EQ:
            case Tag.NOT_EQ:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = relational_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = rest7();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
            default:
                prod.addChilds(new Leaf((byte) 255,"E",0));
                return prod;
        }
    }

    /**
     * and_expression -> equality_expression rest8
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node and_expression() {
        Production prod = new Production("and_expression");
        Node child1 = equality_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest8();
            if (child2 == null) {
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
     * rest8 ->  '&' equality_expression rest8
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest8() {
        Production prod = new Production("rest8");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.AND) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = equality_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest8();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * exclusive_or_expression -> and_expression rest9
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node exclusive_or_expression() {
        Production prod = new Production("exclusive_or_expression");
        Node child1 = and_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest9();
            if (child2 == null) {
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
     * rest9 ->  '^' and_expression rest9
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest9() {
        Production prod = new Production("rest9");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.XOR) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = and_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest9();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * inclusive_or_expression -> exclusive_or_expression rest10
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node inclusive_or_expression() {
        Production prod = new Production("inclusive_or_expression");
        Node child1 = exclusive_or_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest10();
            if (child2 == null) {
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
     * rest10 ->  '|' exclusive_or_expression rest10
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest10() {
        Production prod = new Production("rest10");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.OR) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = exclusive_or_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest10();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * logical_and_expression -> inclusive_or_expression rest11
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node logical_and_expression() {
        Production prod = new Production("logical_and_expression");
        Node child1 = inclusive_or_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest11();
            if (child2 == null) {
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
     * rest11 ->  '&&' inclusive_or_expression rest11
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest11() {
        Production prod = new Production("rest11");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.LOGICAL_AND) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = inclusive_or_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest11();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * logical_or_expression -> logical_and_expression rest12
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node logical_or_expression() {
        Production prod = new Production("logical_or_expression");
        Node child1 = logical_and_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest12();
            if (child2 == null) {
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
     * rest12 ->  '||' logical_and_expression rest12
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest12() {
        Production prod = new Production("rest12");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.LOGICAL_OR) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = logical_and_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest12();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * conditional_expression -> logical_or_expression left4
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node conditional_expression() {
        Production prod = new Production("conditional_expression");
        Node child1 = logical_or_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left4();
            if (child2 == null) {
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
     * left4 ->  '?' expression ':' conditional_expression
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left4() {
        Production prod = new Production("left4");
        Node child1, child2 = null, child3 = null;
        if (getTokenTag() == Tag.QMARK) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = expect(Tag.COLON);
            }
            if (child2 != null) {
                child3 = conditional_expression();
            }
            if (child3 != null && !child3.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.addChilds(child2);
                prod.addChilds(child3);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * assignment_expression ->  conditional_expression
     *                         | unary_expression assignment_operator assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node assignment_expression() {
        Production prod = new Production("assignment_expression");
        Node child1 = unary_expression();
        Node child2 = null, child3;
        if (child1 != null && !child1.getChilds().isEmpty()) {
            child2 = assignment_operator();
        }
        if (child2 != null && !child2.getChilds().isEmpty()) {
            child3 = assignment_expression();
            if (child3 == null || child3.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.addChilds(child2);
                prod.addChilds(child3);
                return prod;
            }
        }
        child1 = conditional_expression();
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
     * assignment_operator -> '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<' | '>>=' | '&=' | '^=' | '|='
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node assignment_operator() {
        Production prod = new Production("assignment_operator");
        Node child1 = accept(Tag.ASSIGNMENT);
        if (child1 != null) {
            prod.addChilds(child1);
        }
        return prod;
    }

    /**
     * expression -> assignment_expression rest13
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node expression() {
        Production prod = new Production("expression");
        Node child1 = assignment_expression();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest13();
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
     * rest13 ->  ',' assignment_expression rest13
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest13() {
        Production prod = new Production("rest13");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.COMMA) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = assignment_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest13();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * constant_expression -> conditional_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node constant_expression() {
        Production prod = new Production("constant_expression");
        Node child1 = conditional_expression();
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
     * declaration -> declaration_specifiers left5
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node declaration() {
        Production prod = new Production("declaration");
        Node child1 = declaration_specifiers();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left5();
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
     * left5 ->  ';'
     *         | init_declarator_list ';'
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left5() {
        Production prod = new Production("left5");
        if (getTokenTag() == Tag.SEMICOLON) {
            prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
            nextToken();
            return prod;
        }
        Node child1 = init_declarator_list();
        Node child2 = null;
        if (child1 == null) {
            //error recovery
            while (getTokenTag() != Tag.SEMICOLON) {
                nextToken();
            }
            nextToken();
            prod.addChilds(new Leaf((byte) 254, "Error", -1));
            return prod;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.SEMICOLON);
        }
        if (child2 != null) {
            prod.addChilds(child1);
            return prod;
        }
        //error recovery
        while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
            nextToken();
        }
        if (getTokenTag() == Tag.SEMICOLON) {
            prod.addChilds(new Leaf((byte) 254, "Error", -1));
            nextToken();
            return prod;
        } else {
            return null;
        }
    }

    /**
     * declaration_specifiers ->  storage_class_specifier left6
     *                          | type_specifier left6
     *                          | type_qualifier left6
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node declaration_specifiers() {
        Production prod = new Production("declaration_specifiers");
        Node child1 = storage_class_specifier();
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = left6();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        child1 = type_specifier(true);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left6();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        child1 = type_qualifier(true);
        if (!child1.getChilds().isEmpty()) {
            child2 = left6();
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
     * left6 ->  declaration_specifiers
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left6() {
        Production prod = new Production("left6");
        Node child1 = declaration_specifiers();
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
     * init_declarator_list -> init_declarator rest14
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node init_declarator_list() {
        Production prod = new Production("init_declarator_lis");
        Node child1 = init_declarator();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest14();
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
     * rest14 ->  ',' init_declarator rest14
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest14() {
        Production prod = new Production("rest14");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.COMMA) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = init_declarator();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest14();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * init_declarator -> declarator left7
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node init_declarator() {
        Production prod = new Production("init_declarator");
        Node child1 = declarator(Kind.VARIABLE);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left7();
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
     * left7 ->  '=' initializer
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left7() {
        Production prod = new Production("left7");
        Leaf terminal;
        if (getTokenValue().equals("=")) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = initializer();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * storage_class_specifier -> TYPEDEF | EXTERN | STATIC | AUTO | REGISTER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
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
     * struct_or_union_specifier -> struct_or_union left8
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node struct_or_union_specifier(boolean flag) {
        Production prod = new Production("struct_or_union_specifier");
        Node child1 = struct_or_union(flag);
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = left8(flag);
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
     * left8 ->  '{' struct_declaration_list '}'
     *         | IDENTIFIER left9
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left8(boolean flag) {
        Production prod = new Production("left8");
        Leaf terminal;
        Node child1, child2 = null;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = struct_declaration_list();
                if (child1 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    nextToken();
                    prod.addChilds(new Leaf((byte) 254, "Error", -1));
                    return prod;
                }
                if (!child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.RIGHT_BRACES);
                }
                if (child2 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    return prod;
                } else {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    if (getTokenTag() == Tag.RIGHT_BRACES) {
                        nextToken();
                        prod.addChilds(new Leaf((byte) 254, "Error", -1));
                        return prod;
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
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left9();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
        }
        return prod;
    }

    /**
     * left9 ->  '{' struct_declaration_list '}'
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left9() {
        Production prod = new Production("left9");
        Leaf terminal;
        Node child1, child2 = null;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = struct_declaration_list();
            if (child1 == null) {
                //error recovery
                while (getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                }
                nextToken();
                prod.addChilds(new Leaf((byte) 254, "Error", -1));
                return prod;
            }
            if (!child1.getChilds().isEmpty()) {
                child2 = expect(Tag.RIGHT_BRACES);
            }
            if (child2 != null) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            } else {
                //error recovery
                while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                }
                if (getTokenTag() == Tag.RIGHT_BRACES) {
                    nextToken();
                    prod.addChilds(new Leaf((byte) 254, "Error", -1));
                    return prod;
                } else {
                    return null;
                }
            }
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * struct_or_union -> STRUCT | UNION
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node struct_or_union(boolean flag) {
        Production prod = new Production("struct_or_union");
        switch (getTokenTag()) {
            case Tag.STRUCT:
            case Tag.UNION:
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
     * struct_declaration_list -> struct_declaration rest15
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node struct_declaration_list() {
        Production prod = new Production("struct_declaration_list");
        Node child1 = struct_declaration();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest15();
            if (child2 == null) {
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
     * rest15 ->  struct_declaration rest15
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest15() {
        Production prod = new Production("rest15");
        Node child1 = struct_declaration();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest15();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }

    /**
     * struct_declaration -> specifier_qualifier_list left10
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node struct_declaration() {
        Production prod = new Production("struct_declaration");
        Node child1 = specifier_qualifier_list(true);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left10();
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
     * left10 ->  ';'
     *          | struct_declarator_list ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left10() {
        Production prod = new Production("left10");
        if (getTokenTag() == Tag.SEMICOLON) {
            prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
            nextToken();
            return prod;
        }
        Node child1 = struct_declarator_list();
        Node child2;
        if (child1 == null) {
            //error recovery
            while (getTokenTag() != Tag.SEMICOLON) {
                nextToken();
            }
            nextToken();
            prod.addChilds(new Leaf((byte) 254, "Error", -1));
            return prod;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.SEMICOLON);
            if (child2 == null) {
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
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            }
        }
        return prod;
    }

    /**
     * specifier_qualifier_list ->  type_specifier left11
     *                            | type_qualifier left11
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node specifier_qualifier_list(boolean flag) {
        Production prod = new Production("specifier_qualifier_ist");
        Node child1 = type_specifier(flag);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left11(flag);
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        child1 = type_qualifier(flag);
        if (!child1.getChilds().isEmpty()) {
            child2 = left11(flag);
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
     * left11 ->  specifier_qualifier_list
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left11(boolean flag) {
        Production prod = new Production("left11");
        Node child1 = specifier_qualifier_list(flag);
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
     * struct_declarator_list -> struct_declarator rest16
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node struct_declarator_list() {
        Production prod = new Production("struct_declarator_list");
        Node child1 = struct_declarator();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest16();
            if (child2 == null) {
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
     * rest16 ->  ',' struct_declarator rest16
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest16() {
        Production prod = new Production("rest16");
        Node child1, child2 = null;
        if (getTokenTag() == Tag.COMMA) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = struct_declarator();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest16();
            }
            if (child2 != null) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * struct_declarator ->  ':' constant_expression
     *                     | declarator left12
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
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
            return null;
        }
        child1 = declarator(Kind.VARIABLE);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left12();
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
     * left12 ->  ':' constant_expression
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left12() {
        Production prod = new Production("left12");
        if (getTokenTag() == Tag.COLON) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = constant_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255, "E", 0));
            return prod;
        }
    }

    /**
     * enum_specifier -> ENUM left13
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node enum_specifier(boolean flag) {
        Production prod = new Production("enum_specifier");
        if (getTokenTag() == Tag.ENUM) {
            //zisťovanie typu identifikátora
            if (flag) {
                type += getTokenValue() + " ";
            }

            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = left13(flag);
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        return prod;
    }

    /**
     * left13 ->  '{' enumerator_list left14
     *          | IDENTIFIER '{' enumerator_list left14
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left13(boolean flag) {
        Production prod = new Production("left13");
        Leaf terminal;
        Node child1, child2 = null, child3 = null;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = enumerator_list();
                if (child1 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    nextToken();
                    prod.addChilds(new Leaf((byte) 254, "Error", -1));
                    return prod;
                }
                if (!child1.getChilds().isEmpty()) {
                    child2 = left14();
                }
                if (child2 == null) {
                    return null;
                }
                if (child2.getChilds().isEmpty()) {
                    //TODO:nie som si istý
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    if (getTokenTag() == Tag.RIGHT_BRACES) {
                        nextToken();
                        prod.addChilds(new Leaf((byte) 254, "Error", -1));
                        return prod;
                    } else {
                        return null;
                    }
                }
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            case Tag.IDENTIFIER:
                if (flag) {
                    //pridanie záznamu do symbolickej tabuľky
                    symbolTable.insert(getTokenValue(), type, getTokenLine());
                    type = "";
                }
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.LEFT_BRACES);
                if (child1 != null) {
                    child2 = enumerator_list();
                }
                if (child2 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    nextToken();
                    prod.addChilds(new Leaf((byte) 254, "Error", -1));
                    return prod;
                }
                if (!child2.getChilds().isEmpty()) {
                    child3 = left14();
                }
                if (child3 == null) {
                    return  null;
                }
                if (child3.getChilds().isEmpty()) {
                    //TODO:nie som si istý
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    if (getTokenTag() == Tag.RIGHT_BRACES) {
                        nextToken();
                        prod.addChilds(new Leaf((byte) 254, "Error", -1));
                        return prod;
                    } else {
                        return null;
                    }
                }
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.addChilds(child2);
                prod.getChilds().addAll(child3.getChilds());
                //prod.addChilds(child3);
                return prod;
        }
        return prod;
    }

    /**
     * left14 ->  '}'
     *          | ',' '}'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left14() {
        Production prod = new Production("left14");
        switch (getTokenTag()) {
            case Tag.RIGHT_BRACES:
                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
            case Tag.COMMA:
                Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                Node child1 = expect(Tag.RIGHT_BRACES);
                if (child1 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    return prod;
                } else {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                        nextToken();
                    }
                    if (getTokenTag() == Tag.RIGHT_BRACES) {
                        nextToken();
                        prod.addChilds(new Leaf((byte) 254, "Error", -1));
                        return prod;
                    } else {
                        return null;
                    }
                }
        }
        return prod;
    }

    /**
     * enumerator_list -> enumerator rest17
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node enumerator_list() {
        Production prod = new Production("enumerator_list");
        Node child1 = enumerator();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest17();
            if (child2 == null) {
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
     * rest17 ->  ',' enumerator rest17
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest17() {
        Production prod = new Production("rest17");
        Node child1, child2 = null;
        if (getTokenTag() == Tag.COMMA) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = enumerator();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest17();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * enumerator -> enumeration_constant left15
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node enumerator() {
        Production prod = new Production("enumerator");
        Node child1 = enumeration_constant();
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = left15();
            if (child2 == null) {
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
     * left15 ->  '=' constant_expression
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left15() {
        Production prod = new Production("left15");
        if (getTokenValue().equals("=")) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = constant_expression();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * type_qualifier -> CONST | VOLATILE
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
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
    private Node declarator(byte kind) {
        Production prod = new Production("declarator");
        Node child1 = pointer(true);
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = direct_declarator(kind);
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(child1);
                prod.addChilds(child2);
            }
            return prod;
        }
        child1 = direct_declarator(kind);
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
    private Node direct_declarator(byte kind) {
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
                    } else {
                        symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.ARRAY);
                    }
                } else if (getTokenTag(position + 1) == Tag.LEFT_BRACKETS) {
                    //ide o funkciu
                    symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.FUNCTION);
                } else {
                    if (kind == Kind.PARAMETER) {
                        symbolTable.insert(getTokenValue(), type, getTokenLine(), Kind.PARAMETER);
                    } else {
                        symbolTable.insert(getTokenValue(), type, getTokenLine());
                    }
                }
                type = "";

                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest18();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = declarator(kind);
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = accept(Tag.RIGHT_BRACKETS);
                }
                if (child2 != null) {
                    child3 = rest18();
                }
                if (child3 != null && !child3.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
                position = pos;
                return prod;
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
    private Node rest18() {
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
                return null;
            case Tag.LEFT_BRACKETS:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left17();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
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
                    child2 = rest18();
                    if (child2 == null) {
                        return null;
                    } else {
                        prod.addChilds(terminal);
                        prod.addChilds(child1);
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
                return null;
            case Tag.RIGHT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest18();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
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
                child3 = rest18();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = type_qualifier_list(false);
        if (child1 == null) {
            return null;
        }
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
    private Node left17() {
        Production prod = new Production("left17");
        Node child1;
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = rest18();
            if (child1 != null) {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        child1 = parameter_type_list();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest18();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
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
            child2 = rest18();
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
     * left18 ->  type_qualifier_list assignment_expression ']' rest18
     *          | assignment_expression ']' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left18() {
        Production prod = new Production("left18");
        Node child1 = type_qualifier_list(false);
        if (child1 == null) {
            return null;
        }
        Node child2, child3, child4;
        if (!child1.getChilds().isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                child3 = expect(Tag.RIGHT_PARENTHESES);
                if (child3 == null) {
                    return null;
                } else {
                    child4 = rest18();
                    if (child4 == null || child4.getChilds().isEmpty()) {
                        return null;
                    } else {
                        prod.addChilds(child1);
                        prod.addChilds(child2);
                        prod.addChilds(child3);
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
                child3 = rest18();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
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
                    child2 = rest18();
                    if (child2 == null) {
                        return null;
                    } else {
                        prod.addChilds(terminal);
                        prod.addChilds(child1);
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
                    child3 = rest18();
                }
                if (child3 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
                return null;
            case Tag.RIGHT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest18();
                if (child1 != null) {
                    prod.addChilds(terminal);
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
                child3 = rest18();
                if (child3 == null ||child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
    }

    /**
     * pointer -> '*' left20
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node pointer(boolean flag) {
        Production prod = new Production("pointer");
        int pos = position;
        if (getTokenTag() == Tag.MULT) {
            //zisťovanie typu identifikátora
            if (flag) {
                type += getTokenValue() + " ";
            }

            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = left20(flag);
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            if (flag) {
                type = type.substring(0, type.length() -2);
            }
        }
        position = pos;
        return prod;
    }

    /**
     * left20 ->  type_qualifier_list left21
     *          | pointer
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left20(boolean flag) {
        Production prod = new Production("left20");
        Node child1 = type_qualifier_list(flag);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left21(flag);
            prod.addChilds(child1);
            prod.getChilds().addAll(child2.getChilds());
            //prod.addChilds(child2);
            return prod;
        }
        child1 = pointer(flag);
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }

    /**
     * left21 ->  pointer
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left21(boolean flag) {
        Production prod = new Production("left21");
        Node child1 = pointer(flag);
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }

    /**
     * type_qualifier_list -> type_qualifier rest19
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node type_qualifier_list(boolean flag) {
        Production prod = new Production("type_qualifier_list");
        Node child1 = type_qualifier(flag);
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = rest19(flag);
            if (child2 == null) {
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
     * rest19 ->  type_qualifier rest19
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest19(boolean flag) {
        Production prod = new Production("rest19");
        Node child1 = type_qualifier(flag);
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = rest19(flag);
            if (child2 == null) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }

    /**
     * parameter_type_list ->  parameter_list left37
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node parameter_type_list() {
        Production prod = new Production("parameter_type_list");
        Node child1 = parameter_list();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left37();
            if (child2 != null) {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        }
        return prod;
    }

    /**
     * left37 ->  ',' '...'
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left37() {
        Production prod = new Production("left37");
        if (getTokenTag() == Tag.COMMA) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = expect(Tag.ELLIPSIS);
            if (child1 != null) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255, "E", 0));
            return prod;
        }
    }

    /**
     * parameter_list -> parameter_declaration rest20
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node parameter_list() {
        Production prod = new Production("parameter_list");
        Node child1 = parameter_declaration();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest20();
            if (child2 == null) {
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
     * rest20 ->  ',' parameter_declaration rest20
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest20() {
        Production prod = new Production("rest20");
        Node child1, child2 = null;
        if (getTokenTag() == Tag.COMMA) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = parameter_declaration();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                child2 = rest20();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * parameter_declaration -> declaration_specifiers left22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node parameter_declaration() {
        Production prod = new Production("parameter_declaration");
        Node child1 = declaration_specifiers();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left22();
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
     * left22 ->  declarator
     *          | abstract_declarator
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left22() {
        Production prod = new Production("left22");
        Node child1 = declarator(Kind.PARAMETER);
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
     * identifier_list -> IDENTIFIER rest21
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node identifier_list() {
        Production prod = new Production("identifier_list");
        if (getTokenTag() == Tag.IDENTIFIER) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = rest21();
            if (child1 != null) {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        return prod;
    }

    /**
     * rest21 ->  ',' IDENTIFIER rest21
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest21() {
        Production prod = new Production("rest21");
        Node child1, child2 = null;
        if (getTokenTag() == Tag.COMMA) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = expect(Tag.IDENTIFIER);
            if (child1 != null) {
                child2 = rest21();
            }
            if (child2 != null && !child2.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * type_name ->  specifier_qualifier_list left23
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node type_name() {
        Production prod = new Production("type_name");
        Node child1 = specifier_qualifier_list(false);
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left23();
            if (child2 == null) {
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
     * left23 ->  abstract_declarator
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left23() {
        Production prod = new Production("left23");
        Node child1 = abstract_declarator();
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
     * abstract_declarator ->  pointer left24
     *                       | direct_abstract_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node abstract_declarator() {
        Production prod = new Production("abstractor_declarator");
        Node child1 = pointer(false);
        Node child2;
        if (!child1.getChilds().isEmpty()) {
            child2 = left24();
            if (child2 != null) {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
            }
            return prod;
        }
        child1 = direct_abstract_declarator();
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
     * left24 ->  direct_abstract_declarator
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left24() {
        Production prod = new Production("left24");
        Node child1 = direct_abstract_declarator();
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
     * direct_abstract_declarator ->  '(' left25
     *                              | '[' left26
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
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
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
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
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                }  else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = parameter_type_list();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                child3 = rest22();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
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
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
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
                    if (child2 == null ||child2.getChilds().isEmpty()) {
                        return null;
                    } else {
                        prod.addChilds(terminal);
                        prod.addChilds(child1);
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
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = type_qualifier_list(false);
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = left28();
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
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = assignment_expression();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                child3 = expect(Tag.RIGHT_PARENTHESES);
                if (child3 == null) {
                    return null;
                } else {
                    child4 = rest22();
                    if (child4 == null || child4.getChilds().isEmpty()) {
                        return null;
                    } else {
                        prod.addChilds(child1);
                        prod.addChilds(child2);
                        prod.addChilds(child3);
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
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
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
                if (child3 != null && !child3.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
                return null;
            case Tag.RIGHT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = rest22();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
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
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
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
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        child1 = parameter_type_list();
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
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
    }

    /**
     * initializer ->  '{' initializer_list left14
     *               | assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node initializer() {
        Production prod = new Production("initializer");
        Node child1, child2 = null;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = initializer_list();
            if (child1 == null) {
                while (getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                }
                nextToken();
                prod.addChilds(new Leaf((byte) 254, "Error", -1));
                return prod;
            }
            if (!child1.getChilds().isEmpty()) {
                child2 = left14();
            }
            if (child2 == null) {
                return null;
            }
            if (child2.getChilds().isEmpty()) {
                //TODO:nie som si istý
                //error recovery
                while (getTokenTag() != Tag.SEMICOLON && getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                }
                if (getTokenTag() == Tag.RIGHT_BRACES) {
                    nextToken();
                    prod.addChilds(new Leaf((byte) 254, "Error", -1));
                    return prod;
                } else {
                    return null;
                }
            }
            prod.addChilds(terminal);
            prod.addChilds(child1);
            prod.getChilds().addAll(child2.getChilds());
            //prod.addChilds(child2);
            return prod;
        }
        child1 = assignment_expression();
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
     * initializer_list ->  designation initializer rest23
     *                    | initializer rest23
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node initializer_list() {
        Production prod = new Production("initialize_list");
        Node child1= designation();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = initializer();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                child3 = rest23();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = initializer();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest23();
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
     * rest23 ->  ',' left30
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest23() {
        Production prod = new Production("rest23");
        if (getTokenTag() == Tag.COMMA) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = left30();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
                return prod;
            }
            return null;
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * left30 ->  designation initializer rest23
     *          | initializer rest23
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left30() {
        Production prod = new Production("left30");
        Node child1 = designation();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = initializer();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                child3 = rest23();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                }  else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = initializer();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest23();
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
     * designation -> designator_list '='
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node designation() {
        Production prod = new Production("designation");
        Node child1 = designator_list();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            if (getTokenValue().equals("=")) {
                prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
                nextToken();
                return prod;
            } else {
                return null;
            }
        }
        return prod;
    }

    /**
     * designator_list -> designator rest24
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node designator_list() {
        Production prod = new Production("designator_list");
        Node child1 = designator();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest24();
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
     * rest24 ->  designator rest24
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest24() {
        Production prod = new Production("rest24");
        Node child1 = designator();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest24();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }

    /**
     * designator ->  '[' constant_expression ']'
     *              | '.' IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node designator() {
        Production prod = new Production("designator");
        Node child1, child2 = null;
        Leaf terminal;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = constant_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.RIGHT_PARENTHESES);
                }
                if (child2 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    return prod;
                }
                return null;
            case Tag.DOT:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.IDENTIFIER);
                if (child1 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    return prod;
                }
                return null;
        }
        return prod;
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
    private Node statement() {
        Production prod = new Production("statement");
        Node child1 = labeled_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = compound_statement();
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = expression_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = selection_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = iteration_statement();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = jump_statement();
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
     * labeled_statement ->  IDENTIFIER ':' statement
     *                     | CASE constant_expression ':' statement
     *                     | DEFAULT ':' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node labeled_statement() {
        Production prod = new Production("labeled_statement");
        int pos = position;
        Node child1, child2 = null, child3 = null;
        Leaf terminal;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = accept(Tag.COLON);
                if (child1 != null) {
                    child2 = statement();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    return prod;
                }
                position = pos;
                break;
            case Tag.DEFAULT:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.COLON);
                if (child1 != null) {
                    child2 = statement();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    return prod;
                }
                return null;
            case Tag.CASE:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = constant_expression();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.COLON);
                }
                if (child2 != null) {
                    child3 = statement();
                }
                if (child3 != null && !child3.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.addChilds(child3);
                    return prod;
                }
                return null;
        }
        return prod;
    }

    /**
     * compound_statement -> '{' left31
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node compound_statement() {
        Production prod = new Production("compound_statement");
        if (getTokenTag() == Tag.LEFT_BRACES) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = left31();
            if (child1.getChilds().isEmpty()) {
                //error recovery
                while (getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                }
                nextToken();
                prod.addChilds(new Leaf((byte) 254, "Error", -1));
            } else {
                prod.addChilds(terminal);
                prod.getChilds().addAll(child1.getChilds());
                //prod.addChilds(child1);
            }
            return prod;
        }
        return prod;
    }

    /**
     * left31 ->  '}'
     *          | block_item_list '}'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left31() {
        Production prod = new Production("left31");
        if (getTokenTag() == Tag.RIGHT_BRACES) {
            prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
            nextToken();
            return prod;
        }
        SymbolTable parent = symbolTable;
        //vytvorenie vnorenej tabuľky
        symbolTable = new SymbolTable(symbolTable);
        if (parent != null) {
            parent.addChild(symbolTable);
        }
        Node child1 = block_item_list();
        Node child2;
        if (child1 == null) {
            //error recovery
            while (getTokenTag() != Tag.RIGHT_BRACES) {
                nextToken();
            }
            nextToken();
            prod.addChilds(new Leaf((byte) 254, "Error", -1));
            return prod;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACES);
            if (child2 == null) {
                //error recovery
                while (getTokenTag() != Tag.RIGHT_BRACES) {
                    nextToken();
                }
                nextToken();
                prod.addChilds(new Leaf((byte) 254, "Error", -1));
            } else {
                symbolTable = parent;
                prod.addChilds(child1);
                prod.addChilds(child2);
            }
            return prod;
        }
        return prod;
    }

    /**
     * block_item_list ->  block_item rest25
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node block_item_list() {
        Production prod = new Production("block_item_list");
        Node child1 = block_item();
        Node child2;
        if (child1 != null && !child1.getChilds().isEmpty()) {
            child2 = rest25();
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
     * rest25->  block_item rest25
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest25() {
        Production prod = new Production("rest25");
        Node child1 = block_item();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest25();
            if (child2 == null) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }

    /**
     * block_item ->  declaration
     *              | statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node block_item() {
        Production prod = new Production("block_item");
        Node child1 = declaration();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = statement();
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
     * expression_statement ->  ';'
     *                        | expression ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node expression_statement() {
        Production prod = new Production("expression_statement");
        if (getTokenTag() == Tag.SEMICOLON) {
            prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
            nextToken();
            return prod;
        }
        Node child1 = expression();
        Node child2;
        if (child1 == null) {
            //error recovery
            while (getTokenTag() != Tag.SEMICOLON) {
                nextToken();
            }
            nextToken();
            prod.addChilds(new Leaf((byte) 254, "Error", -1));
            return prod;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.SEMICOLON);
            if (child2 == null) {
                //error recovery
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
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            }
        }
        return prod;
    }

    /**
     * selection_statement ->  IF '(' expression ')' statement left32
     *                       | SWITCH '(' expression ')' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node selection_statement() {
        Production prod = new Production("selection_statement");
        Node child1, child2 = null, child3 = null, child4 = null, child5 = null;
        Leaf terminal;
        switch (getTokenTag()) {
            case Tag.IF:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    child3 = expect(Tag. RIGHT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = statement();
                }
                if (child4 != null && !child4.getChilds().isEmpty()) {
                    child5 = left32();
                }
                if (child5 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.addChilds(child3);
                    prod.addChilds(child4);
                    prod.getChilds().addAll(child5.getChilds());
                    //prod.addChilds(child5);
                    return prod;
                }
                return null;
            case Tag.SWITCH:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    child3 = expect(Tag. RIGHT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = statement();
                }
                if (child4 != null && !child4.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.addChilds(child3);
                    prod.addChilds(child4);
                    return prod;
                }
                return null;
        }
        return prod;
    }

    /**
     * left32 ->  ELSE statement
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node left32() {
        Production prod = new Production("left32");
        if (getTokenTag() == Tag.ELSE) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            Node child1 = statement();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                return prod;
            } else {
                return null;
            }
        } else {
            prod.addChilds(new Leaf((byte) 255,"E",0));
            return prod;
        }
    }

    /**
     * iteration_statement ->  WHILE '(' expression ')' statement
     *                       | DO statement WHILE '(' espression ')' ';'
     *                       | FOR '(' left33
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node iteration_statement() {
        Production prod = new Production("iteration_statement");
        Leaf terminal;
        Node child1, child2 = null, child3 = null, child4 = null;
        Node child5 = null, child6 = null;
        switch (getTokenTag()) {
            case Tag.WHILE:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag. LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = expression();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    child3 = expect(Tag. RIGHT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = statement();
                }
                if (child4 != null && !child4.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.addChilds(child3);
                    prod.addChilds(child4);
                    return prod;
                }
                return null;
            case Tag.DO:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = statement();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    child2 = expect(Tag.WHILE);
                }
                if (child2 != null) {
                    child3 = expect(Tag. LEFT_BRACKETS);
                }
                if (child3 != null) {
                    child4 = expression();
                }
                if (child4 != null && !child4.getChilds().isEmpty()) {
                    child5 = expect(Tag. RIGHT_BRACKETS);
                }
                if (child5 != null) {
                    child6 = expect(Tag.SEMICOLON);
                }
                if (child1 == null || child2 == null || child3 == null || child4 == null || child5 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON) {
                        nextToken();
                    }
                    nextToken();
                    prod.addChilds(new Leaf((byte) 254, "Error", -1));
                    return prod;
                }
                if (child6 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.addChilds(child3);
                    prod.addChilds(child4);
                    prod.addChilds(child5);
                    prod.addChilds(child6);
                    return prod;
                } else {
                    //error recovery
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
                }
            case Tag.FOR:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag. LEFT_BRACKETS);
                if (child1 != null) {
                    child2 = left33();
                }
                if (child2 != null && !child2.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.getChilds().addAll(child2.getChilds());
                    //prod.addChilds(child2);
                    return prod;
                }
                return null;
        }
        return prod;
    }

    /**
     * left33 ->  expression_statement expression_statement left34
     *          | declaration expression_statement left34
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left33() {
        Production prod = new Production("left33");
        Node child1 = expression_statement();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expression_statement();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                child3 = left34();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        child1 = declaration();
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expression_statement();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                child3 = left34();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.getChilds().addAll(child3.getChilds());
                    //prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
    }

    /**
     * left34 ->  ')' statement
     *          | expression ')' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left34() {
        Production prod = new Production("left34");
        Node child1;
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            Leaf terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
            nextToken();
            child1 = statement();
            if (child1 != null && !child1.getChilds().isEmpty()) {
                prod.addChilds(terminal);
                prod.addChilds(child1);
                return prod;
            }
            return null;
        }
        child1 = expression();
        Node child2, child3;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.RIGHT_BRACKETS);
            if (child2 == null) {
                return null;
            } else {
                child3 = statement();
                if (child3 == null || child3.getChilds().isEmpty()) {
                    return null;
                } else {
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    prod.addChilds(child3);
                    return prod;
                }
            }
        }
        return prod;
    }

    /**
     * jump_statement ->  GOTO IDENTIFIER ';'
     *                  | CONTINUE ';'
     *                  | BREAK ';'
     *                  | RETURN left35
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node jump_statement() {
        Production prod = new Production("jump_statement");
        Leaf terminal;
        Node child1, child2;
        switch (getTokenTag()) {
            case Tag.GOTO:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.IDENTIFIER);
                if (child1 == null) {
                    //error recovery
                    while (getTokenTag() != Tag.SEMICOLON) {
                        nextToken();
                    }
                    nextToken();
                    prod.addChilds(new Leaf((byte) 254, "Error", -1));
                    return prod;
                } else {
                    child2 = expect(Tag.SEMICOLON);
                }
                if (child2 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    prod.addChilds(child2);
                    return prod;
                } else {
                    //error recovery
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
                }
            case Tag.CONTINUE:
            case Tag.BREAK:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = expect(Tag.SEMICOLON);
                if (child1 != null) {
                    prod.addChilds(terminal);
                    prod.addChilds(child1);
                    return prod;
                } else {
                    //error recovery
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
                }
            case Tag.RETURN:
                terminal = new Leaf(getTokenTag(), getTokenValue(), getTokenLine());
                nextToken();
                child1 = left35();
                if (child1 != null && !child1.getChilds().isEmpty()) {
                    prod.addChilds(terminal);
                    prod.getChilds().addAll(child1.getChilds());
                    //prod.addChilds(child1);
                    return prod;
                }
                return null;
        }
        return prod;
    }

    /**
     * left35 ->  ';'
     *          | expression ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node left35() {
        Production prod = new Production("left35");
        if (getTokenTag() == Tag.SEMICOLON) {
            prod.addChilds(new Leaf(getTokenTag(), getTokenValue(), getTokenLine()));
            nextToken();
            return prod;
        }
        Node child1 = expression();
        Node child2;
        if (child1 == null) {
            //error recovery
            while (getTokenTag() != Tag.SEMICOLON) {
                nextToken();
            }
            nextToken();
            prod.addChilds(new Leaf((byte) 254, "Error", -1));
            return prod;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = expect(Tag.SEMICOLON);
            if (child2 == null) {
                //error recovery
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
                prod.addChilds(child1);
                prod.addChilds(child2);
                return prod;
            }
        }
        return prod;
    }

    /**
     * translation_unit -> external_declaration rest26
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node translation_unit() {
        Production prod = new Production("translation_unit");
        Node child1 = external_declaration();
        Node child2;
        if (child1 != null && !child1.getChilds().isEmpty()) {
            child2 = rest26();
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
     * rest26 ->  external_declaration rest26
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private Node rest26() {
        Production prod = new Production("rest26");
        Node child1 = external_declaration();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest26();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }

    /**
     * external_declaration ->  function_definition
     *                        | declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node external_declaration() {
        Production prod = new Production("external_declaration");
        Node child1 = function_definition();
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        child1 = declaration();
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
     * function_definition -> declaration_specifiers declarator left36
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node function_definition() {
        Production prod = new Production("functional_definiton");
        Node child1 = declaration_specifiers();
        Node child2 = null, child3 = null;
        if(child1 != null && !child1.getChilds().isEmpty()) {
            child2 = declarator(Kind.FUNCTION);
        }
        if (child2 != null && !child2.getChilds().isEmpty()) {
            child3 = left36();
        }
        if (child3 != null && !child3.getChilds().isEmpty()) {
            prod.addChilds(child1);
            prod.addChilds(child2);
            prod.getChilds().addAll(child3.getChilds());
            //prod.addChilds(child3);
            return prod;
        }
        return prod;
    }

    /**
     * left36 ->  declaration_list compound_statement
     *          | compound_statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private Node left36() {
        Production prod = new Production("left36");
        Node child1 = declaration_list();
        Node child2 = null;
        if (child1 != null && !child1.getChilds().isEmpty()) {
            child2 = compound_statement();
        }
        if (child2 != null && !child2.getChilds().isEmpty()) {
            prod.addChilds(child1);
            prod.addChilds(child2);
            return prod;
        }
        child1 = compound_statement();
        if (!child1.getChilds().isEmpty()) {
            prod.addChilds(child1);
            return prod;
        }
        return prod;
    }

    /**
     * declaration_list -> declaration rest27
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node declaration_list() {
        Production prod = new Production("declaration_list");
        Node child1 = declaration();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest27();
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
     * rest27 ->  declaration rest27
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private Node rest27() {
        Production prod = new Production("rest27");
        Node child1 = declaration();
        Node child2;
        if (child1 == null) {
            return null;
        }
        if (!child1.getChilds().isEmpty()) {
            child2 = rest27();
            if (child2 == null || child2.getChilds().isEmpty()) {
                return null;
            } else {
                prod.addChilds(child1);
                prod.getChilds().addAll(child2.getChilds());
                //prod.addChilds(child2);
                return prod;
            }
        }
        prod.addChilds(new Leaf((byte) 255,"E",0));
        return prod;
    }
}