package Compiler.Parser;

import Compiler.Lexer.Scanner;
import Compiler.Lexer.Token;
import Compiler.Lexer.Tag;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private int position = 0;
    private ArrayList<Token> tokenStream = new ArrayList<Token>();

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

    private String getTokenValue() {
        return tokenStream.get(position).value;
    }

    private int accept(byte tag) {
        if (tokenStream.get(position).tag == tag) {
            nextToken();
            return 1;
        }
        return 0;
    }

    private int expect(byte tag) {
        if (accept(tag) == 1) {
            return 1;
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
        return 0;
    }

    private byte getTokenTag() {
        return tokenStream.get(position).tag;
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
    private int primary_expression() {
        int pos = position;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
            case Tag.CHARACTER:
            case Tag.STRING:
                nextToken();
                return 1;
            case Tag.LEFT_BRACKETS:
                nextToken();
                int value = expression();
                if (value == 1) {
                    value = accept(Tag.RIGHT_BRACKETS);
                }
                if (value == 1) {
                    return 1;
                }
                position = pos;
                return 0;
            default:
                if (constant() == 1) {
                    return 1;
                } else {
                    return 0;
                }
        }
    }

    /**
     * constant ->  NUMBER
     *            | REAL
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int constant() {
        switch (getTokenTag()) {
            case Tag.NUMBER:
            case Tag.REAL:
                nextToken();
                return 1;
            default:
                return 0;
        }
    }

    /**
     * enumeration_constant -> IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int enumeration_constant() {
        return accept(Tag.IDENTIFIER);
    }

    /**
     * postfix_expression ->  primary_expression rest1
     *                      | '(' type_name ')' '{' initializer_list '}' left1
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int postfix_expression() {
        int pos = position;
        int value = 0;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            nextToken();
            value = type_name();
            if (value == 1) {
                value = accept(Tag.RIGHT_BRACKETS);
            }
            if (value == 1) {
                value = accept(Tag.LEFT_BRACES);
            }
            if (value == 1) {
                value = initializer_list();
            }
            if (value == 1) {
                value = accept(Tag.RIGHT_BRACES);
            }
            if (value == 1) {
                value = left1();
            }
            if (value == 1) {
                return 1;
            }
            position = pos;
        }
        value = primary_expression();
        if (value == 1) {
            value = rest1();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
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
    private int rest1() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                nextToken();
                value = expression();
                if (value == 1) {
                    value = expect(Tag.RIGHT_PARENTHESES);
                }
                if (value == 1) {
                    value = rest1();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.DOT:
            case Tag.ARROW:
                nextToken();
                value = expect(Tag.IDENTIFIER);
                if (value == 1) {
                    value = rest1();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.INC:
            case Tag.DEC:
                nextToken();
                if (rest1() == 1) {
                    return 1;
                }
                return -1;
            case Tag.LEFT_BRACKETS:
                nextToken();
                if (left2() == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
        }
    }

    /**
     * left1 ->  '}' rest1
     *         | ',' '}' rest1
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left1() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.RIGHT_BRACES:
                nextToken();
                value = rest1();
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.COMMA:
                nextToken();
                value = expect(Tag.RIGHT_BRACES);
                if (value == 1) {
                    value = rest1();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            default:
                return 0;
        }
    }

    /**
     * left2 ->  ')' rest1
     *         | argument_expression_list ')' rest1
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left2() {
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            nextToken();
            if (rest1() == 1) {
                return 1;
            }
            return -1;
        }
        int value = argument_expression_list();
        if (value == 1) {
            value = expect(Tag.RIGHT_BRACKETS);
        }
        if (value == 1) {
            value = rest1();
        }
        if (value == 1) {
            return 1;
        }
        return -1;
    }

    /**
     * argument_expression_list -> assignment_expression rest2
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int argument_expression_list() {
        int value = assignment_expression();
        if (value == 1) {
            value = rest2();
        }
        if (value == 1) {
            return 1;
        }
        return -1;
    }

    /**
     * rest2 ->  ',' assignment_expression rest2
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest2() {
        int value = 0;
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            value = assignment_expression();
            if (value == 1) {
                value = rest2();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
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
    private int unary_expression() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.INC:
            case Tag.DEC:
                nextToken();
                value = unary_expression();
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.SIZEOF:
                nextToken();
                value = left3();
                if (value == 1) {
                    return 1;
                }
                return -1;
        }
        value = unary_operator();
        if (value == 1) {
            value = cast_expression();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = postfix_expression();
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     *  left3 ->  unary_expression
     *          | '(' type_name ')'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left3() {
        int value = unary_expression();
        if (value == 1) {
            return 1;
        }
        if (getTokenTag() == Tag.LEFT_BRACKETS) {
            nextToken();
            value = type_name();
            if (value == 1) {
                value = expect(Tag.RIGHT_BRACKETS);
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        }
        return 0;
    }

    /**
     * unary_operator -> '&' | '*' | '+' | '-' | '~' | '!'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int unary_operator() {
        switch (getTokenTag()) {
            case Tag.AND:
            case Tag.MULT:
            case Tag.PLUS:
            case Tag.MINUS:
            case Tag.BITWISE_NOT:
            case Tag.LOGICAL_NOT:
                return 1;
            default:
                return 0;
        }
    }

    /**
     * cast_expression ->  unary_expression
     *                   | '(' type_name ')' cast_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int cast_expression() {
        int pos = position;
        int value = 0;
        if (getTokenTag() == Tag.LEFT_BRACKETS) {
            nextToken();
            value = type_name();
            if (value == 1) {
                value = accept(Tag.RIGHT_BRACKETS);
            }
            if (value == 1) {
                value = cast_expression();
            }
            if (value == 1) {
                return 1;
            }
            position = pos;
        }
        value = unary_expression();
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * multiplicative_expression -> cast_expression rest3
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int multiplicative_expression() {
        int value = cast_expression();
        if (value == 1) {
            value = rest3();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
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
    private int rest3() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.MULT:
            case Tag.DIV:
            case Tag.MOD:
                nextToken();
                value = cast_expression();
                if (value == 1) {
                    value = rest3();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
        }
    }

    /**
     * additive_expression -> multiplicative_expression rest4
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int additive_expression() {
        int value = multiplicative_expression();
        if (value == 1) {
            value = rest4();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest4 ->  '+' multiplicative_expression rest4
     *         | '-' multiplicative_expression rest4
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest4() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.PLUS:
            case Tag.MINUS:
                nextToken();
                value = multiplicative_expression();
                if (value == 1) {
                    value = rest4();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
        }
    }

    /**
     * shift_expression -> additive_expression rest5
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int shift_expression() {
        int value = additive_expression();
        if (value == 1) {
            value = rest5();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest5 ->  '<<' additive_expression rest5
     *         | '>>' additive_expression rest5
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest5() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.LEFT_SHIFT:
            case Tag.RIGHT_SHIFT:
                nextToken();
                value = additive_expression();
                if (value == 1) {
                    value = rest5();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
        }
    }

    /**
     * relational_expression -> shift_expression rest6
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int relational_expression() {
        int value = shift_expression();
        if (value == 1) {
            value = rest6();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
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
    private int rest6() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.LT:
            case Tag.GT:
            case Tag.LEQT:
            case Tag.GEQT:
                nextToken();
                value = shift_expression();
                if (value == 1) {
                    value = rest6();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
        }
    }

    /**
     * equality_expression -> relational_expression rest7
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int equality_expression() {
        int value = relational_expression();
        if (value == 1) {
            value = rest7();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest7 ->  '==' relational_expression rest7
     *         | '!=' relational_expression rest7
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest7() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.EQ:
            case Tag.NOT_EQ:
                nextToken();
                value = relational_expression();
                if (value == 1) {
                    value = rest7();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
        }
    }

    /**
     * and_expression -> equality_expression rest8
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int and_expression() {
        int value = equality_expression();
        if (value == 1) {
            value = rest8();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest8 ->  '&' equality_expression rest8
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest8() {
        int value = 0;
        if (getTokenTag() == Tag.AND) {
            nextToken();
            value = equality_expression();
            if (value == 1) {
                value = rest8();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * exclusive_or_expression -> and_expression rest9
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int exclusive_or_expression() {
        int value = and_expression();
        if (value == 1) {
            value = rest9();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest9 ->  '^' and_expression rest9
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest9() {
        int value = 0;
        if (getTokenTag() == Tag.XOR) {
            nextToken();
            value = and_expression();
            if (value == 1) {
                value = rest9();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * inclusive_or_expression -> exclusive_or_expression rest10
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int inclusive_or_expression() {
        int value = exclusive_or_expression();
        if (value == 1) {
            value = rest10();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest10 ->  '|' exclusive_or_expression rest10
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest10() {
        int value = 0;
        if (getTokenTag() == Tag.OR) {
            nextToken();
            value = exclusive_or_expression();
            if (value == 1) {
                value = rest10();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * logical_and_expression -> inclusive_or_expression rest11
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int logical_and_expression() {
        int value = inclusive_or_expression();
        if (value == 1) {
            value = rest11();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest11 ->  '&&' inclusive_or_expression rest11
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest11() {
        int value = 0;
        if (getTokenTag() == Tag.LOGICAL_AND) {
            nextToken();
            value = inclusive_or_expression();
            if (value == 1) {
                value = rest11();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * logical_or_expression -> logical_and_expression rest12
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int logical_or_expression() {
        int value = logical_and_expression();
        if (value == 1) {
            value = rest12();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest12 ->  '||' logical_and_expression rest12
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest12() {
        int value = 0;
        if (getTokenTag() == Tag.LOGICAL_OR) {
            nextToken();
            value = logical_and_expression();
            if (value == 1) {
                value = rest12();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * conditional_expression -> logical_or_expression left4
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int conditional_expression() {
        int value = logical_or_expression();
        if (value == 1) {
            value = left4();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left4 ->  '?' expression ':' conditional_expression
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left4() {
        int value = 0;
        if (getTokenTag() == Tag.QMARK) {
            nextToken();
            value = expression();
            if (value == 1) {
                value = expect(Tag.COLON);
            }
            if (value == 1) {
                value = conditional_expression();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * assignment_expression ->  conditional_expression
     *                         | unary_expression assignment_operator assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int assignment_expression() {
        int value = unary_expression();
        if (value == 1) {
            value = assignment_operator();
        }
        if (value == 1) {
            value = assignment_expression();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = conditional_expression();
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * assignment_operator -> '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<' | '>>=' | '&=' | '^=' | '|='
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int assignment_operator() {
        return accept(Tag.ASSIGNMENT);
    }

    /**
     * expression -> assignment_expression rest13
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int expression() {
        int value = assignment_expression();
        if (value == 1) {
            value = rest13();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest13 ->  ',' assignment_expression rest13
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest13() {
        int value = 0;
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            value = assignment_expression();
            if (value == 1) {
                value = rest13();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * constant_expression -> conditional_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int constant_expression() {
        if (conditional_expression() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * declaration -> declaration_specifiers left5
     * @return
     */
    private int declaration() {
        int value = declaration_specifiers();
        if (value == 1) {
            value = left5();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left5 ->  ';'
     *         | init_declarator_list ';'
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left5() {
        if (getTokenTag() == Tag.SEMICOLON) {
            nextToken();
            return 1;
        }
        int value = init_declarator_list();
        if (value == 1) {
            value = expect(Tag.SEMICOLON);
        }
        if (value == 1) {
            return 1;
        }
        return -1;
    }

    /**
     * declaration_specifiers ->  storage_class_specifier left6
     *                          | type_specifier left6
     *                          | type_qualifier left6
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int declaration_specifiers() {
        int value = storage_class_specifier();
        if (value == 1) {
            value = left6();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = type_specifier();
        if (value == 1) {
            value = left6();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = type_qualifier();
        if (value == 1) {
            value = left6();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left6 ->  declaration_specifiers
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left6() {
        if (declaration_specifiers() == -1) {
            return -1;
        }
        return 1;
    }

    /**
     * init_declarator_list -> init_declarator rest14
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int init_declarator_list() {
        int value = init_declarator();
        if (value == 1) {
            value = rest14();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest 14 ->  ',' init_declarator rest14
     *           | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest14() {
        int value = 0;
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            value = init_declarator();
            if (value == 1) {
                value = rest14();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * init_declarator -> declarator left7
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int init_declarator() {
        int value = declarator();
        if (value == 1) {
            value = left7();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left7 ->  '=' initializer
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left7() {
        int value = 0;
        if (getTokenValue().equals("=")) {
            nextToken();
            value = initializer();
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * storage_class_specifier -> TYPEDEF | EXTERN | STATIC | AUTO | REGISTER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int storage_class_specifier() {
        switch (getTokenTag()) {
            case Tag.TYPEDEF:
            case Tag.EXTERN:
            case Tag.STATIC:
            case Tag.AUTO:
            case Tag.REGISTER:
                nextToken();
                return 1;
        }
        return 0;
    }

    /**
     * type_specifier ->  VOID | CHAR | SHORT | INT | LONG | FLOAT | DOUBLE | SIGNED | UNSIGNED
     *                  | struct_or_union_specifier
     *                  | enum_specifier
     *                  | TYPEDEF_NAME ???
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int type_specifier() {
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
                nextToken();
                return 1;
        }
        if (struct_or_union_specifier() == 1) {
            return 1;
        }
        if (enum_specifier() == 1) {
            return 1;
        }
        //TODO: TYPEDEF_NAME vyriešiť
        return 0;
    }

    /**
     * struct_or_union_specifier -> struct_or_union left8
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int struct_or_union_specifier() {
        int value = struct_or_union();
        if (value == 1) {
            value = left8();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left8 ->  '{' struct_declaration_list '}'
     *         | IDENTIFIER left9
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left8() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACES:
                nextToken();
                value = struct_declaration_list();
                if (value == 1) {
                    value = expect(Tag.RIGHT_BRACES);
                }
                if (value == 1) {
                    return 0;
                }
                return -1;
            case Tag.IDENTIFIER:
                nextToken();
                value = left9();
                if (value == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * left9 ->  '{' struct_declaration_list '}'
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left9() {
        int value = 0;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            nextToken();
            value = struct_declaration_list();
            if (value == 1) {
                value = expect(Tag.RIGHT_BRACES);
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * struct_or_union -> STRUCT | UNION
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int struct_or_union() {
        switch (getTokenTag()) {
            case Tag.STRUCT:
            case Tag.UNION:
                nextToken();
                return 1;
        }
        return 0;
    }

    /**
     * struct_declaration_list -> struct_declaration rest15
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int struct_declaration_list() {
        int value = struct_declaration();
        if (value == 1) {
            value = rest15();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest15 ->  struct_declaration rest15
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest15() {
        int value = struct_declaration();
        if (value == 1) {
            value = rest15();
            if (value < 1) {
                return -1;
            }
        }
        return 1;
    }

    /**
     * struct_declaration -> specifier_qualifier_list left10
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int struct_declaration() {
        int value = specifier_qualifier_list();
        if (value == 1) {
            value = left10();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left10 ->  ';'
     *          | struct_declarator_list ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left10() {
        if (getTokenTag() == Tag.SEMICOLON) {
            nextToken();
            return 1;
        }
        int value = struct_declarator_list();
        if (value == 1) {
            value = expect(Tag.SEMICOLON);
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * specifier_qualifier_list ->  type_specifier left11
     *                            | type_qualifier left11
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int specifier_qualifier_list() {
        int value = type_specifier();
        if (value == 1) {
            value = left11();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = type_qualifier();
        if (value == 1) {
            value = left11();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left11 ->  specifier_qualifier_list
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left11() {
        if (specifier_qualifier_list() == -1) {
            return -1;
        }
        return 1;
    }

    /**
     * struct_declarator_list -> struct_declarator rest16
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int struct_declarator_list() {
        int value = struct_declarator();
        if (value == 1) {
            value = rest16();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest16 ->  ',' struct_declarator rest16
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest16() {
        int value = 0;
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            value = struct_declarator();
            if (value == 1) {
                value = rest16();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * struct_declarator ->  ':' constant_expression
     *                     | declarator left12
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int struct_declarator() {
        if (getTokenTag() == Tag.COLON) {
            nextToken();
            if (constant_expression() == 1) {
                return  1;
            }
            return -1;
        }
        int value = declarator();
        if (value == 1) {
            value = left12();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left12 ->  ':' constant_expression
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left12() {
        if (getTokenTag() == Tag.COLON) {
            nextToken();
            if (constant_expression() == 1) {
                return 1;
            }
            return -1;
        }
        return 1;
    }

    /**
     * enum_specifier -> ENUM left13
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int enum_specifier() {
        if (getTokenTag() == Tag.ENUM) {
            nextToken();
            if (left13() == 1) {
                return 1;
            }
            return -1;
        }
        return 0;
    }

    /**
     * left13 ->  '{' enumerator_list left14
     *          | IDENTIFIER '{' enumerator_list left14
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left13() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.LEFT_BRACES:
                nextToken();
                value = enumerator_list();
                if (value == 1) {
                    value = left14();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.IDENTIFIER:
                nextToken();
                value = expect(Tag.LEFT_BRACES);
                if (value == 1) {
                    value = enumerator_list();
                }
                if (value == 1) {
                    value = left14();
                }
                if (value == 1) {
                    return 1;
                };
                return -1;
        }
        return 0;
    }

    /**
     * left14 ->  '}'
     *          | ',' '}'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left14() {
        switch (getTokenTag()) {
            case Tag.RIGHT_BRACES:
                nextToken();
                return 1;
            case Tag.COMMA:
                nextToken();
                if (expect(Tag.RIGHT_BRACES) == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * enumerator_list -> enumerator rest17
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int enumerator_list() {
        int value = enumerator();
        if (value == 1) {
            value = rest17();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest17 ->  ',' enumerator rest17
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest17() {
        int value = 0;
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            value = enumerator();
            if (value == 1) {
                value = rest17();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * enumerator -> enumeration_constant left15
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int enumerator() {
        int value = enumeration_constant();
        if (value == 1) {
            value = left15();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left15 ->  '=' constant_expression
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left15() {
        if (getTokenValue().equals("=")) {
            nextToken();
            if (constant_expression() == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * type_qualifier -> CONST | VOLATILE
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int type_qualifier() {
        switch (getTokenTag()) {
            case Tag.CONST:
            case Tag.VOLATILE:
                nextToken();
                return 1;
        }
        return 0;
    }

    /**
     * declarator ->  pointer direct_declarator
     *              | direct_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int declarator() {
        int value = pointer();
        if (value == 1) {
            value = direct_declarator();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        if (direct_declarator() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * direct_declarator ->  IDENTIFIER rest18
     *                     | '(' declarator ')' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int direct_declarator() {
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                nextToken();
                if (rest18() == 1) {
                    return 1;
                }
                return -1;
            case Tag.LEFT_BRACKETS:
                nextToken();
                int value = declarator();
                if (value == 1) {
                    value = expect(Tag.RIGHT_BRACKETS);
                }
                if (value == 1) {
                    value = rest18();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * rest18 ->  '[' left16
     *          | '(' left17
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int rest18() {
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                nextToken();
                if (left16() == 1) {
                    return 1;
                }
                return -1;
            case Tag.LEFT_BRACKETS:
                nextToken();
                if (left17() == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
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
    private int left16() {
        int pos = position;
        int value = 0;
        switch (getTokenTag()) {
            case Tag.MULT:
                nextToken();
                value = accept(Tag.RIGHT_PARENTHESES);
                if (value == 1) {
                    value = rest18();
                    if (value < 1) {
                        return -1;
                    }
                } else {
                    position = pos;
                    break;
                }
                if (value == 1) {
                    return 1;
                }
            case Tag.STATIC:
                nextToken();
                if (left18() == 1) {
                    return 1;
                }
                return -1;
            case Tag.RIGHT_PARENTHESES:
                nextToken();
                if (rest18() == 1) {
                    return 1;
                }
                return -1;
        }
        value = assignment_expression();
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest18();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = type_qualifier_list();
        if (value == 1) {
            value = left19();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left17 ->  parameter_type_list ')' rest18
     *          | ')' rest18
     *          | identifier_list rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left17() {
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            nextToken();
            if (rest18() == 1) {
                return 1;
            }
            return -1;
        }
        int value = parameter_type_list();
        if (value == 1) {
            value = expect(Tag.RIGHT_BRACKETS);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest18();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = identifier_list();
        if (value == 1) {
            value = rest18();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left18 ->  type_qualifier_list assignment_expression ']' rest18
     *          | assignment_expression ']' rest18
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left18() {
        int value = type_qualifier_list();
        if (value == 1) {
            value = assignment_expression();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest18();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = assignment_expression();
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest18();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
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
    private int left19() {
        int pos = position;
        int value = 0;
        switch (getTokenTag()) {
            case Tag.MULT:
                nextToken();
                value = accept(Tag.RIGHT_PARENTHESES);
                if (value == 1) {
                    value = rest18();
                    if (value < 1) {
                        return -1;
                    }
                } else {
                    position = pos;
                    break;
                }
                if (value == 1) {
                    return 1;
                }
            case Tag.STATIC:
                nextToken();
                value = assignment_expression();
                if (value == 1) {
                    value = expect(Tag.RIGHT_PARENTHESES);
                }
                if (value == 1) {
                    value = rest18();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.RIGHT_PARENTHESES:
                nextToken();
                if (rest18() == 1) {
                    return 1;
                }
                return -1;
        }
        value = assignment_expression();
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest18();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * pointer -> '*' left20
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int pointer() {
        if (getTokenTag() == Tag.MULT) {
            nextToken();
            if (left20() == 1) {
                return 1;
            }
            return -1;
        }
        return 0;
    }

    /**
     * left20 ->  type_qualifier_list left21
     *          | pointer
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left20() {
        int value = type_qualifier_list();
        if (value == 1) {
            value = left21();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        if (pointer() == -1) {
            return -1;
        }
        return 1;
    }

    /**
     * left21 ->  pointer
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left21() {
        if (pointer() == -1) {
            return -1;
        }
        return 1;
    }

    /**
     * type_qualifier_list -> type_qualifier rest19
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int type_qualifier_list() {
        int value = type_qualifier();
        if (value == 1) {
            value = rest19();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest19 ->  type_qualifier rest19
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest19() {
        if (type_qualifier() == 1) {
            if (rest19() < 1) {
                return -1;
            }
        }
        return 1;
    }

    /**
     * parameter_type_list ->  parameter_list ',' '...' ????
     *                       | parameter_list
     * @return
     */
    //TODO: pozrieť sa na to ??
    private int parameter_type_list() {
        return 0;
    }

    /**
     * parameter_list -> parameter_declaration rest20
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int parameter_list() {
        int value = parameter_declaration();
        if (value == 1) {
            value = rest20();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest20 ->  ',' parameter_declaration rest20
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest20() {
        int value = 0;
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            value = parameter_declaration();
            if (value == 1) {
                value = rest20();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * parameter_declaration -> declaration_specifiers left22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int parameter_declaration() {
        int value = declaration_specifiers();
        if (value == 1) {
            value = left22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left22 ->  declarator
     *          | abstract_declarator
     *          | epsilon
     * @return
     */
    //TODO:upraviť
    private int left22() {
        if (declarator() == 1) {
            return 1;
        }
        abstract_declarator();
        return 1;
    }

    /**
     * identifier_list -> IDENTIFIER rest21
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int identifier_list() {
        if (getTokenTag() == Tag.IDENTIFIER) {
            nextToken();
            if (rest21() == 1) {
                return 1;
            }
            return -1;
        }
        return 0;
    }

    /**
     * rest21 ->  ',' IDENTIFIER rest21
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest21() {
        int value = 0;
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            value = expect(Tag.IDENTIFIER);
            if (value == 1) {
                value = rest21();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * type_name ->  specifier_qualifier_list left23
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int type_name() {
        int value = specifier_qualifier_list();
        if (value == 1) {
            value = left23();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left23 ->  abstract_declarator
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left23() {
        if (abstract_declarator() == -1) {
            return -1;
        }
        return 1;
    }

    /**
     * abstract_declarator ->  pointer left24
     *                       | direct_abstract_declarator
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int abstract_declarator() {
        int value = pointer();
        if (value == 1) {
            value = left24();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        if (direct_abstract_declarator() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left24 ->  direct_abstract_declarator
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left24() {
        if (direct_abstract_declarator() == -1) {
            return -1;
        }
        return 1;
    }

    /**
     * direct_abstract_declarator ->  '(' left25
     *                              | '[' left26
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int direct_abstract_declarator() {
        switch (getTokenTag()) {
            case Tag.LEFT_BRACKETS:
                nextToken();
                if (left25() == 1) {
                    return 1;
                }
                return -1;
            case Tag.LEFT_PARENTHESES:
                nextToken();
                if (left26() == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * left25 ->  abstract_declarator ')' rest22
     *          | ')' rest22
     *          | parameter_type_list ')' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left25() {
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            nextToken();
            if (rest22() == 1) {
                return 1;
            }
            return -1;
        }
        int value = abstract_declarator();
        if (value == 1) {
            value = expect(Tag.RIGHT_BRACKETS);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = parameter_type_list();
        if (value == 1) {
            value = expect(Tag.RIGHT_BRACKETS);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
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
    private int left26() {
        int pos = position;
        int value = 0;
        switch (getTokenTag()) {
            case Tag.RIGHT_PARENTHESES:
                nextToken();
                if (rest22() == 1) {
                    return 1;
                }
                return -1;
            case Tag.MULT:
                nextToken();
                value = accept(Tag.RIGHT_PARENTHESES);
                if (value == 1) {
                    value = rest22();
                    if (value < 1) {
                        return -1;
                    }
                } else {
                    position = pos;
                    break;
                }
                if (value == 1) {
                    return 1;
                }
            case Tag.STATIC:
                nextToken();
                if (left27() == 1) {
                    return 1;
                }
                return -1;
        }
        value = assignment_expression();
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = type_qualifier_list();
        if (value == 1) {
            value = left28();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left27 ->  type_qualifier_list assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left27() {
        int value = type_qualifier_list();
        if (value == 1) {
            value = assignment_expression();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = assignment_expression();
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left28 ->  STATIC assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     *          | ']' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left28() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.STATIC:
                nextToken();
                value = assignment_expression();
                if (value == 1) {
                    value = expect(Tag.RIGHT_PARENTHESES);
                }
                if (value == 1) {
                    value = rest22();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.RIGHT_PARENTHESES:
                nextToken();
                if (rest22() == 1) {
                    return 1;
                }
                return -1;
        }
        value = assignment_expression();
        if (value == 1) {
            value = expect(Tag.RIGHT_PARENTHESES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest22 ->  '[' left26
     *          | '(' left29
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest22() {
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                nextToken();
                if (left26() == 1) {
                    return 1;
                }
                return -1;
            case Tag.LEFT_BRACKETS:
                nextToken();
                if (left29() == 1) {
                    return 1;
                }
                return -1;
            default:
                return 1;
        }
    }

    /**
     * left29 ->  ')' rest22
     *          | parameter_type_list ')' rest22
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left29() {
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            nextToken();
            if (rest22() == 1) {
                return 1;
            }
            return -1;
        }
        int value = parameter_type_list();
        if (value == 1) {
            value = expect(Tag.RIGHT_BRACKETS);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest22();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * initializer ->  '{' initializer_list left14
     *               | assignment_expression
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int initializer() {
        int value = 0;
        if (getTokenTag() == Tag.LEFT_BRACES) {
            nextToken();
            value = initializer_list();
            if (value == 1) {
                value = left14();
            }
            if (value == 1) {
                return 1;
            }
            return -1;
        }
        if (assignment_expression() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * initializer_list ->  designation initializer rest23
     *                    | initializer rest23
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int initializer_list() {
        int value = designation();
        if (value == 1) {
            value = initializer();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest23();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = initializer();
        if (value == 1) {
            value = rest23();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest23 ->  ',' left30
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest23() {
        if (getTokenTag() == Tag.COMMA) {
            nextToken();
            if (left30() == 1) {
                return 1;
            }
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * left30 ->  designation initializer rest23
     *          | initializer rest23
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left30() {
        int value = designation();
        if (value == 1) {
            value = initializer();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            value = rest23();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = initializer();
        if (value == 1) {
            value = rest23();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * designation -> designator_list '='
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int designation() {
        int value = designator_list();
        if (value == 1) {
            if (getTokenValue().equals("=")) {
                value = 1;
                nextToken();
            } else {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * designator_list -> designator rest24
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int designator_list() {
        int value = designator();
        if (value == 1) {
            value = rest24();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest24 ->  designator rest24
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest24() {
        int value = designator();
        if (value == 1) {
            if (rest24() < 1) {
                return -1;
            }
        }
        return 1;
    }

    /**
     * designator ->  '[' constant_expression ']'
     *              | '.' IDENTIFIER
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int designator() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.LEFT_PARENTHESES:
                nextToken();
                value = constant_expression();
                if (value == 1) {
                    value = expect(Tag.RIGHT_PARENTHESES);
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.DOT:
                nextToken();
                if (expect(Tag.IDENTIFIER) == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
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
    //TODO: pozrieť sa na chyby
    private int statement() {
        if (labeled_statement() == 1) {
            return 1;
        }
        if (compound_statement() == 1) {
            return 1;
        }
        if (expression_statement() == 1) {
            return 1;
        }
        if (selection_statement() == 1) {
            return 1;
        }
        if (iteration_statement() == 1) {
            return 1;
        }
        if (jump_statement() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * labeled_statement ->  IDENTIFIER ':' statement
     *                     | CASE constant_expression ':' statement
     *                     | DEFAULT ':' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int labeled_statement() {
        int pos = position;
        int value = 0;
        switch (getTokenTag()) {
            case Tag.IDENTIFIER:
                nextToken();
                value = accept(Tag.COLON);
                if (value == 1) {
                    value = statement();
                }
                if (value == 1) {
                    return 1;
                }
                position = pos;
                break;
            case Tag.DEFAULT:
                nextToken();
                value = expect(Tag.COLON);
                if (value == 1) {
                    value = statement();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.CASE:
                nextToken();
                value = constant_expression();
                if (value == 1) {
                    value = expect(Tag.COLON);
                }
                if (value == 1) {
                    value = statement();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * compound_statement -> '{' left31
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int compound_statement() {
        if (getTokenTag() == Tag.LEFT_BRACES) {
            nextToken();
            if (left31() == 1) {
                return 1;
            }
            return -1;
        }
        return 0;
    }

    /**
     * left31 ->  '}'
     *          | block_item_list '}'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left31() {
        if (getTokenTag() == Tag.RIGHT_BRACES) {
            nextToken();
            return 1;
        }
        int value = block_item_list();
        if (value == 1) {
            value = expect(Tag.RIGHT_BRACES);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * block_item_list ->  block_item rest25
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int block_item_list() {
        int value = block_item();
        if (value == 1) {
            value = rest25();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest25->  block_item rest25
     *         | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest25() {
        int value = block_item();
        if (value == 1) {
            if (rest25() < 1) {
                return -1;
            }
        }
        return 1;
    }

    /**
     * block_item ->  declaration
     *              | statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    //TODO: pozrieť sa na chyby
    private int block_item() {
        if (declaration() == 1) {
            return 1;
        }
        if (statement() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * expression_statement ->  ';'
     *                        | expression ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int expression_statement() {
        if (getTokenTag() == Tag.SEMICOLON) {
            nextToken();
            return 1;
        }
        int value = expression();
        if (value == 1) {
            value = expect(Tag.SEMICOLON);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * selection_statement ->  IF '(' expression ')' statement left32
     *                       | SWITCH '(' expression ')' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int selection_statement() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.IF:
                nextToken();
                value = expect(Tag.LEFT_BRACKETS);
                if (value == 1) {
                    value = expression();
                }
                if (value == 1) {
                    value = expect(Tag. RIGHT_BRACKETS);
                }
                if (value == 1) {
                    value = statement();
                }
                if (value == 1) {
                    value = left32();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.SWITCH:
                nextToken();
                value = expect(Tag.LEFT_BRACKETS);
                if (value == 1) {
                    value = expression();
                }
                if (value == 1) {
                    value = expect(Tag. RIGHT_BRACKETS);
                }
                if (value == 1) {
                    value = statement();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * left32 ->  ELSE statement
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int left32() {
        if (getTokenTag() == Tag.ELSE) {
            nextToken();
            if (statement() == 1) {
                return 1;
            } else {
                return -1;
            }
        }
        return 1;
    }

    /**
     * iteration_statement ->  WHILE '(' expression ')' statement
     *                       | DO statement WHILE '(' espression ')' ';'
     *                       | FOR '(' left33
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int iteration_statement() {
        int value = 0;
        switch (getTokenTag()) {
            case Tag.WHILE:
                nextToken();
                value = expect(Tag. LEFT_BRACKETS);
                if (value == 1) {
                    value = expression();
                }
                if (value == 1) {
                    value = expect(Tag. RIGHT_BRACKETS);
                }
                if (value == 1) {
                    value = statement();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.DO:
                nextToken();
                value = statement();
                if (value == 1) {
                    value = expect(Tag.WHILE);
                }
                if (value == 1) {
                    value = expect(Tag. LEFT_BRACKETS);
                }
                if (value == 1) {
                    value = expression();
                }
                if (value == 1) {
                    value = expect(Tag. RIGHT_BRACKETS);
                }
                if (value == 1) {
                    value = expect(Tag.SEMICOLON);
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.FOR:
                nextToken();
                value = expect(Tag. LEFT_BRACKETS);
                if (value == 1) {
                    value = left33();
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * left33 ->  expression_statement expression_statement left34
     *          | declaration expression_statement left34
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left33() {
        int value = expression_statement();
        if (value == 1) {
            value = expression_statement();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            value = left34();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        value = declaration();
        if (value == 1) {
            value = expression_statement();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            value = left34();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left34 ->  ')' statement
     *          | expression ')' statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left34() {
        if (getTokenTag() == Tag.RIGHT_BRACKETS) {
            nextToken();
            if (statement() == 1) {
                return 1;
            }
            return -1;
        }
        int value = expression();
        if (value == 1) {
            value = expect(Tag.RIGHT_BRACKETS);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            value = statement();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
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
    private int jump_statement() {
        switch (getTokenTag()) {
            case Tag.GOTO:
                nextToken();
                int value = expect(Tag.IDENTIFIER);
                if (value == 1) {
                    value = expect(Tag.SEMICOLON);
                }
                if (value == 1) {
                    return 1;
                }
                return -1;
            case Tag.CONTINUE:
            case Tag.BREAK:
                nextToken();
                if (expect(Tag.SEMICOLON) == 1) {
                    return 1;
                }
                return -1;
            case Tag.RETURN:
                nextToken();
                if (left35() == 1) {
                    return 1;
                }
                return -1;
        }
        return 0;
    }

    /**
     * left35 ->  ';'
     *          | expression ';'
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int left35() {
        if (getTokenTag() == Tag.SEMICOLON) {
            nextToken();
            return 1;
        }
        int value = expression();
        if (value == 1) {
            value = expect(Tag.SEMICOLON);
            if (value == 0) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * translation_unit -> external_declaration rest26
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int translation_unit() {
        int value = external_declaration();
        if (value == 1) {
            value = rest26();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest26 ->  external_declaration rest26
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         -1 ak sa vyskytla chyba
     */
    private int rest26() {
        int value = external_declaration();
        if (value == 1) {
            if (rest26() < 1) {
                return -1;
            }
        }
        return 1;
    }

    /**
     * external_declaration ->  function_definition
     *                        | declaration
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    //TODO: pozrieť sa na chyby
    private int external_declaration() {
        if (function_definition() == 1) {
            return 1;
        }
        if (declaration() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * function_definition -> declaration_specifiers declarator left36
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int function_definition() {
        int value = declaration_specifiers();
        if(value == 1) {
            value = declarator();
        }
        if (value == 1) {
            value = left36();
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * left36 ->  declaration_list compound_statement
     *          | compound_statement
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     */
    private int left36() {
        int value = declaration_list();
        if (value == 1) {
            value = compound_statement();
        }
        if (value == 1) {
            return 1;
        }
        if (compound_statement() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * declaration_list -> declaration rest27
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int declaration_list() {
        int value = declaration();
        if (value == 1) {
            value = rest27();
            if (value < 1) {
                return -1;
            }
        }
        if (value == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * rest27 ->  declaration rest27
     *          | epsilon
     * @return 1 ak sa našla zhoda,
     *         0 ak sa zhoda nenašla
     *         -1 ak sa vyskytla chyba
     */
    private int rest27() {
        int value = declaration();
        if (value == 1) {
            if (rest27() < 1) {
                return -1;
            }
        }
        return 1;
    }
}