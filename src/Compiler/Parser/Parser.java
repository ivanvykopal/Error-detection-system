package Compiler.Parser;

import Compiler.Lexer.Scanner;
import Compiler.Lexer.Token;
import Compiler.Lexer.Tag;
import java.io.IOException;

public class Parser {
    private Token token;
    private Scanner scanner;

    public Parser(String file) {
        scanner = new Scanner(file);
    }

    private void nextToken() {
        try {
            token = scanner.scan();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean accept(byte tag) {
        if (token.tag == tag) {
            nextToken();
            return true;
        }
        return false;
    }

    /**
     * primary_expression -> IDENTIFIER
     *                      | constant
     *                      | STRING
     *                      | CHAR
     *                      | '(' expression ')'
     * @return
     */
    private int primary_expression() {
        switch (token.tag) {
            case Tag.IDENTIFIER:
                break;
            case Tag.STRING:
                break;
            case Tag.CHARACTER:
                break;
            case Tag.LEFT_BRACKETS:
                break;
            default:
                constant();
                break;
        }
        return 0;
    }

    /**
     * constant ->  NUMBER
     *            | REAL
     * @return
     */
    private int constant() {
        switch (token.tag) {
            case Tag.NUMBER:
                break;
            case Tag.REAL:
                break;
        }
        return 0;
    }

    /**
     * enumeration_constant -> IDENTIFIER
     * @return
     */
    private int enumeration_constant() {
        if (token.tag == Tag.IDENTIFIER) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * postfix_expression ->  primary_expression rest1
     *                      | '(' type_name ')' '{' initializer_list '}' left1
     * @return
     */
    private int postfix_expression() {
        if (token.tag == Tag.LEFT_BRACES) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * rest1 ->  '[' expression ']' rest1
     *         | '.' IDENTIFIER rest1
     *         | '->' IDENTIFIER rest1
     *         | '++' rest1
     *         | '--' rest1
     *         | '(' left2
     *         | epsilon
     * @return
     */
    private int rest1() {
        switch (token.tag) {
            case Tag.LEFT_PARENTHESES:
                break;
            case Tag.DOT:
                break;
            case Tag.ARROW:
                break;
            case Tag.LEFT_BRACKETS:
                break;
            default:
                //TODO: upraviť
                if (token.value.equals("++")) {

                } else if (token.value.equals("--")) {

                } else {
                    //epsilon
                }
        }
        return 0;
    }

    /**
     * left1 ->  '}' rest1
     *         | ',' '}' rest1
     * @return
     */
    private int left1() {
        if (token.tag == Tag.RIGHT_BRACES) {

        } else if (token.tag == Tag.COMMA) {

        }
        else {
            return 0;
        }
        return 0;
    }

    /**
     * left2 ->  ')' rest1
     *         | argument_expression_list ')' rest1
     * @return
     */
    private int left2() {
        if (token.tag == Tag.RIGHT_BRACKETS) {

        }
        else {

        }
        return 0;
    }

    /**
     * argument_expression_list -> assignment_expression rest2
     * @return
     */
    private int argument_expression_list() {
        return 0;
    }

    /**
     * rest2 ->  ',' assignment_expression rest2
     *         | epsilon
     * @return
     */
    private int rest2() {
        if (token.tag == Tag.COMMA) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * unary_expression ->  postfix_expression
     *                    | '++' unary_expression
     *                    | '--' unary_expreesion
     *                    | unary_operator cast_expression
     *                    | SIZEOF left3
     * @return
     */
    private int unary_expression() {
        if (token.value.equals("++")) {

        } else if (token.value.equals("--")) {

        } else if (token.tag == Tag.SIZEOF) {

        }
        return 0;
    }

    /**
     *  left3 ->  unary_expression
     *          | '(' type_name ')'
     * @return
     */
    private int left3() {
        if (token.tag == Tag.LEFT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * unary_operator -> '&' | '*' | '+' | '-' | '~' | '!'
     * @return
     */
    private int unary_operator() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * cast_expression ->  unary_expression
     *                   | '(' type_name ')' cast_expression
     * @return
     */
    private int cast_expression() {
        if (token.tag == Tag.LEFT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * multiplicative_expression -> cast_expression rest3
     * @return
     */
    private int multiplicative_expression() {
        return 0;
    }

    /**
     * rest3 ->  '*' cast_expression rest3
     *         | '/' cast_expression rest3
     *         | '%' cast_expression rest3
     *         | epsilon
     * @return
     */
    private int rest3() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * additive_expression -> multiplicative_expression rest4
     * @return
     */
    private int additive_expression() {
        return 0;
    }

    /**
     * rest4 ->  '+' multiplicative_expression rest4
     *         | '-' multiplicative_expression rest4
     *         | epsilon
     * @return
     */
    private int rest4() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * shift_expression -> additive_expression rest5
     * @return
     */
    private int shift_expression() {
        return 0;
    }

    /**
     * rest5 ->  '<<' additive_expression rest5
     *         | '>>' additive_expression rest5
     *         | epsilon
     * @return
     */
    private int rest5() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * relational_expression -> shift_expression rest6
     * @return
     */
    private int relational_expression() {
        return 0;
    }

    /**
     * rest6 ->  '<' shift_expression rest6
     *         | '>' shift_expression rest6
     *         | '<=' shift_expression rest6
     *         | '>=' shift_expression rest6
     *         | epsilon
     * @return
     */
    private int rest6() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * equality_expression -> relational_expression rest7
     * @return
     */
    private int equality_expression() {
        return 0;
    }

    /**
     * rest7 ->  '==' relational_expression rest7
     *         | '!=' relational_expression rest7
     *         | epsilon
     * @return
     */
    private int rest7() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * and_expression -> equality_expression rest8
     * @return
     */
    private int and_expression() {
        return 0;
    }

    /**
     * rest8 ->  '&' equality_expression rest8
     *         | epsilon
     * @return
     */
    private int rest8() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * exclusive_or_expression -> and_expression rest9
     * @return
     */
    private int exclusive_or_expression() {
        return 0;
    }

    /**
     * rest9 ->  '^' and_expression rest9
     *         | epsilon
     * @return
     */
    private int rest9() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * inclusive_or_expression -> exclusive_or_expression rest10
     * @return
     */
    private int inclusive_or_expression() {
        return 0;
    }

    /**
     * rest10 ->  '|' exclusive_or_expression rest10
     *          | epsilon
     * @return
     */
    private int rest10() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * logical_and_expression -> inclusive_or_expression rest11
     * @return
     */
    private int logical_and_expression() {
        return 0;
    }

    /**
     * rest11 ->  '&&' inclusive_or_expression rest11
     *          | epsilon
     * @return
     */
    private int rest11() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * logical_or_expression -> logical_and_expression rest12
     * @return
     */
    private int logical_or_expression() {
        return 0;
    }

    /**
     * rest12 ->  '||' logical_and_expression rest12
     *          | epsilon
     * @return
     */
    private int rest12() {
        //TODO: rozdeliť Tagy
        return 0;
    }

    /**
     * conditional_expression -> logical_or_expression left4
     * @return
     */
    private int conditional_expression() {
        return 0;
    }

    /**
     * left4 ->  '?' expression ':' conditional_expression
     *         | epsilon
     * @return
     */
    private int left4() {
        if (token.tag == Tag.QMARK) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * assignment_expression ->  conditional_expression
     *                         | unary_expression assignment_operator assignment_expression
     * @return
     */
    private int assignment_expression() {
        return 0;
    }

    /**
     * assignment_operator -> '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<' | '>>=' | '&=' | '^=' | '|='
     * @return
     */
    private int assignment_operator() {
        if (token.tag == Tag.OP_ASSIGNMENT) {

        } else {

        }
        return 0;
    }

    /**
     * expression -> assignment_expression rest13
     * @return
     */
    private int expression() {
        return 0;
    }

    /**
     * rest13 ->  ',' assignment_expression rest13
     *          | epsilon
     * @return
     */
    private int rest13() {
        if (token.tag == Tag.COMMA) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * constant_expression -> conditional_expression
     * @return
     */
    private int constant_expression() {
        return 0;
    }

    /**
     * declaration -> declaration_specifiers left5
     * @return
     */
    private int declaration() {
        return 0;
    }

    /**
     * left5 ->  ';'
     *         | init_declarator_list ';'
     * @return
     */
    private int left5() {
        if (token.tag == Tag.SEMICOLON) {

        } else {

        }
        return 0;
    }

    /**
     * declaration_specifiers ->  storage_class_specifier left6
     *                          | type_specifier left6
     *                          | type_qualifier left6
     * @return
     */
    private int declaration_specifiers() {
        return 0;
    }

    /**
     * left6 ->  declaration_specifiers
     *         | epsilon
     * @return
     */
    private int left6() {
        return 0;
    }

    /**
     * init_declarator_list -> init_declarator rest14
     * @return
     */
    private int init_declarator_list() {
        return 0;
    }

    /**
     * rest 14 ->  ',' init_declarator rest14
     *           | epsilon
     * @return
     */
    private int rest14() {
        if (token.tag == Tag.COMMA) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * init_declarator -> declarator left7
     * @return
     */
    private int init_declarator() {
        return 0;
    }

    /**
     * left7 ->  '=' initializer
     *         | epsilon
     * @return
     */
    private int left7() {
        if (token.value.equals("=")) {

        } else {
            //espilon
        }
        return 0;
    }

    /**
     * storage_class_specifier -> TYPEDEF | EXTERN | STATIC | AUTO | REGISTER
     * @return
     */
    private int storage_class_specifier() {
        switch (token.tag) {
            case Tag.TYPEDEF:
                break;
            case Tag.EXTERN:
                break;
            case Tag.STATIC:
                break;
            case Tag.AUTO:
                break;
            case Tag.REGISTER:
                break;
        }
        return 0;
    }

    /**
     * type_specifier ->  VOID | CHAR | SHORT | INT | LONG | FLOAT | DOUBLE | SIGNED | UNSIGNED
     *                  | struct_or_union_specifier
     *                  | enum_specifier
     *                  | TYPEDEF_NAME ???
     * @return
     */
    private int type_specifier() {
        switch (token.tag) {
            case Tag.VOID:
                break;
            case Tag.CHAR:
                break;
            case Tag.SHORT:
                break;
            case Tag.INT:
                break;
            case Tag.LONG:
                break;
            case Tag.FLOAT:
                break;
            case Tag.DOUBLE:
                break;
            case Tag.SIGNED:
                break;
            case Tag.UNSIGNED:
                break;
        }
        return 0;
    }

    /**
     * struct_or_union_specifier -> struct_or_union left8
     * @return
     */
    private int struct_or_union_specifier() {
        return 0;
    }

    /**
     * left8 ->  '{' struct_declaration_list '}'
     *         | IDENTIFIER left9
     * @return
     */
    private int left8() {
        if (token.tag == Tag.LEFT_BRACES) {

        } else if (token.tag == Tag.IDENTIFIER) {

        }
        return 0;
    }

    /**
     * left9 ->  '{' struct_declaration_list '}'
     *         | epsilon
     * @return
     */
    private int left9() {
        if (token.tag == Tag.LEFT_BRACES) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * struct_or_union -> STRUCT | UNION
     * @return
     */
    private int struct_or_union() {
        if (token.tag == Tag.STRUCT) {

        } else if (token.tag == Tag.UNION) {

        }
        return 0;
    }

    /**
     * struct_declaration_list -> struct_declaration rest15
     * @return
     */
    private int struct_declaration_list() {
        return 0;
    }

    /**
     * rest15 ->  struct_declaration rest15
     *          | epsilon
     * @return
     */
    private int rest15() {
        return 0;
    }

    /**
     * struct_declaration -> specifier_qualifier_list left10
     * @return
     */
    private int struct_declaration() {
        return 0;
    }

    /**
     * left10 ->  ';'
     *          | struct_declarator_list ';'
     * @return
     */
    private int left10() {
        if (token.tag == Tag.SEMICOLON) {

        } else {

        }
        return 0;
    }

    /**
     * specifier_qualifier_list ->  type_specifier left11
     *                            | type_qualifier left11
     * @return
     */
    private int specifier_qualifier_list() {
        return 0;
    }

    /**
     * left11 ->  specifier_qualifier_list
     *          | epsilon
     * @return
     */
    private int left11() {
        return 0;
    }

    /**
     * struct_declarator_list -> struct_declarator rest16
     * @return
     */
    private int struct_declarator_list() {
        return 0;
    }

    /**
     * rest16 ->  ',' struct_declarator rest16
     *          | epsilon
     * @return
     */
    private int rest16() {
        if (token.tag == Tag.COMMA) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * struct_declarator ->  ':' constant_expression
     *                     | declarator left12
     * @return
     */
    private int struct_declarator() {
        if (token.tag == Tag.COLON) {

        } else {

        }
        return 0;
    }

    /**
     * left12 ->  ':' constant_expression
     *          | epsilon
     * @return
     */
    private int left12() {
        if (token.tag == Tag.COLON) {

        } else {
            //espilon
        }
        return 0;
    }

    /**
     * enum_specifier -> ENUM left13
     * @return
     */
    private int enum_specifier() {
        if (token.tag == Tag.ENUM) {

        } else {

        }
        return 0;
    }

    /**
     * left13 ->  '{' enumerator_list left14
     *          | IDENTIFIER '{' enumerator_list left14
     * @return
     */
    private int left13() {
        if (token.tag == Tag.LEFT_BRACES) {

        } else if (token.tag == Tag.IDENTIFIER) {

        } else {

        }
        return 0;
    }

    /**
     * left14 ->  '}'
     *          | ',' '}'
     * @return
     */
    private int left14() {
        if (token.tag == Tag.RIGHT_BRACES) {

        } else if (token.tag == Tag.COMMA) {

        } else {

        }
        return 0;
    }

    /**
     * enumerator_list -> enumerator rest17
     * @return
     */
    private int enumerator_list() {
        return 0;
    }

    /**
     * rest17 ->  ',' enumerator rest17
     *          | epsilon
     * @return
     */
    private int rest17() {
        if (token.tag == Tag.COMMA) {

        } else {

        }
        return 0;
    }

    /**
     * enumerator -> enumeration_constant left15
     * @return
     */
    private int enumerator() {
        return 0;
    }

    /**
     * left15 ->  '=' constant_expression
     *          | epsilon
     * @return
     */
    private int left15() {
        if (token.value.equals("=")) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * type_qualifier -> CONST | RESTRICT | VOLATILE
     * TODO: Pozrieť sa na RESTRICT
     * @return
     */
    private int type_qualifier() {
        switch (token.tag) {
            case Tag.CONST:
                break;
            case Tag.VOLATILE:
                break;
        }
        return 0;
    }

    /**
     * declarator ->  pointer direct_declarator
     *              | direct_declarator
     * @return
     */
    private int declarator() {
        return 0;
    }

    /**
     * direct_declarator ->  IDENTIFIER rest18
     *                     | '(' declarator ')' rest18
     * @return
     */
    private int direct_declarator() {
        if (token.tag == Tag.IDENTIFIER) {

        } else if (token.tag == Tag.LEFT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * rest18 ->  '[' left16
     *          | '(' left17
     *          | epsilon
     * @return
     */
    private int rest18() {
        if (token.tag == Tag.LEFT_PARENTHESES) {

        } else if (token.tag == Tag.LEFT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * left16 ->  '*' ']' rest18
     *          | STATIC left18
     *          | type_qualifier_list left19
     *          | assignment_expression ']' rest18
     *          | ']' rest18
     * @return
     */
    private int left16() {
        //TODO: dokončiť a rozdeliť tagy
        switch (token.tag) {
            case Tag.STATIC:
                break;
            case Tag.RIGHT_PARENTHESES:
                break;
        }
        return 0;
    }

    /**
     * left17 ->  parameter_type_list ')' rest18
     *          | ')' rest18
     *          | identifier_list rest18
     * @return
     */
    private int left17() {
        if (token.tag == Tag.RIGHT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * left18 ->  type_qualifier_list assignment_expression ']' rest18
     *          | assignment_expression ']' rest18
     * @return
     */
    private int left18() {
        return 0;
    }

    /**
     * left19 ->  '*' ']' rest18
     *          | STATIC assignment_expression ']' rest18
     *          | assignment_expression ']' rest18
     *          | ']' rest18
     * @return
     */
    private int left19() {
        switch (token.tag) {
            case Tag.STATIC:
                break;
            case Tag.RIGHT_PARENTHESES:
                break;
        }
        return 0;
    }

    /**
     * pointer -> '*' left20
     * @return
     */
    private int pointer() {
        //TODO: rozdeliť tagy
        return 0;
    }

    /**
     * left20 ->  type_qualifier_list left21
     *          | pointer
     *          | epsilon
     * @return
     */
    private int left20() {
        return 0;
    }

    /**
     * left21 ->  pointer
     *          | epsilon
     * @return
     */
    private int left21() {
        return 0;
    }

    /**
     * type_qualifier_list -> type_qualifier rest19
     * @return
     */
    private int type_qualifier_list() {
        return 0;
    }

    /**
     * rest19 ->  type_qualifier rest19
     *          | epsilon
     * @return
     */
    private int rest19() {
        return 0;
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
     * @return
     */
    private int parameter_list() {
        return 0;
    }

    /**
     * rest20 ->  ',' parameter_declaration rest20
     *          | epsilon
     * @return
     */
    private int rest20() {
        if (token.tag == Tag.COMMA) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * parameter_declaration -> declaration_specifiers left22
     * @return
     */
    private int parameter_declaration() {
        return 0;
    }

    /**
     * left22 ->  declarator
     *          | abstract_declarator
     *          | epsilon
     * @return
     */
    private int left22() {
        return 0;
    }

    /**
     * identifier_list -> IDENTIFIER rest21
     * @return
     */
    private int identifier_list() {
        if (token.tag == Tag.IDENTIFIER) {

        } else {

        }
        return 0;
    }

    /**
     * rest21 ->  ',' IDENTIFIER rest21
     *          | epsilon
     * @return
     */
    private int rest21() {
        if (token.tag == Tag.COMMA) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * type_name ->  specifier_qualifier_list left23
     *
     * @return
     */
    private int type_name() {
        return 0;
    }

    /**
     * left23 ->  abstract_declarator
     *          | epsilon
     * @return
     */
    private int left23() {
        return 0;
    }

    /**
     * abstract_declarator ->  pointer left24
     *                       | direct_abstract_declarator
     * @return
     */
    private int abstract_declarator() {
        return 0;
    }

    /**
     * left24 ->  direct_abstract_declarator
     *          | epsilon
     * @return
     */
    private int left24() {
        return 0;
    }

    /**
     * direct_abstract_declarator ->  '(' left25
     *                              | '[' left26
     * @return
     */
    private int direct_abstract_declarator() {
        switch (token.tag) {
            case Tag.LEFT_BRACKETS:
                break;
            case Tag.LEFT_PARENTHESES:
                break;
        }
        return 0;
    }

    /**
     * left25 ->  abstract_declarator ')' rest22
     *          | ')' rest22
     *          | parameter_type_list ')' rest22
     * @return
     */
    private int left25() {
        if (token.tag == Tag.RIGHT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * left26 ->  ']' rest22
     *          | '*' ']' rest22
     *          | STATIC left27
     *          | type_qualifier_list left28
     *          | assignment_expression ']' rest22
     * @return
     */
    private int left26() {
        switch (token.tag) {
            case Tag.RIGHT_PARENTHESES:
                break;
            case Tag.STATIC:
                break;
        }
        return 0;
    }

    /**
     * left27 ->  type_qualifier_list assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     * @return
     */
    private int left27() {
        return 0;
    }

    /**
     * left28 ->  STATIC assignment_expression ']' rest22
     *          | assignment_expression ']' rest22
     *          | ']' rest22
     * @return
     */
    private int left28() {
        switch (token.tag) {
            case Tag.STATIC:
                break;
            case Tag.RIGHT_PARENTHESES:
                break;
        }
        return 0;
    }

    /**
     * rest22 ->  '[' left26
     *          | '(' left29
     *          | epsilon
     * @return
     */
    private int rest22() {
        switch (token.tag) {
            case Tag.LEFT_PARENTHESES:
                break;
            case Tag.LEFT_BRACKETS:
                break;
            default:
                //epsilon
                break;
        }
        return 0;
    }

    /**
     * left29 ->  ')' rest22
     *          | parameter_type_list ')' rest22
     * @return
     */
    private int left29() {
        if (token.tag == Tag.RIGHT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * initializer ->  '{' initializer_list left14
     *               | assignment_expression
     * @return
     */
    private int initializer() {
        if (token.tag == Tag.LEFT_BRACES) {

        } else {

        }
        return 0;
    }

    /**
     * initializer_list ->  designation initializer rest23
     *                    | initializer rest23
     * @return
     */
    private int initializer_list() {
        return 0;
    }

    /**
     * rest23 ->  ',' left30
     *          | epsilon
     * @return
     */
    private int rest23() {
        if (token.tag == Tag.COMMA) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * left30 ->  designation initializer rest23
     *          | initializer rest23
     * @return
     */
    private int left30() {
        return 0;
    }

    /**
     * designation -> designator_list '='
     * @return
     */
    private int designation() {
        return 0;
    }

    /**
     * designator_list -> designator rest24
     * @return
     */
    private int designator_list() {
        return 0;
    }

    /**
     * rest24 ->  designator rest24
     *          | epsilon
     * @return
     */
    private int rest24() {
        return 0;
    }

    /**
     * designator ->  '[' constant_expression ']'
     *              | '.' IDENTIFIER
     * @return
     */
    private int designator() {
        if (token.tag == Tag.LEFT_PARENTHESES) {

        } else if (token.tag == Tag.DOT) {

        } else {

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
     * @return
     */
    private int statement() {
        return 0;
    }

    /**
     * labeled_statement ->  IDENTIFIER ':' statement
     *                     | CASE constant_expression ':' statement
     *                     | DEFAULT ':' statement
     * @return
     */
    private int labeled_statement() {
        switch (token.tag) {
            case Tag.IDENTIFIER:
                break;
            case Tag.CASE:
                break;
            case Tag.DEFAULT:
                break;
            default:
                break;
        }
        return 0;
    }

    /**
     * compound_statement -> '{' left31
     * @return
     */
    private int compound_statement() {
        if (token.tag == Tag.LEFT_BRACES) {

        } else {

        }
        return 0;
    }

    /**
     * left31 ->  '}'
     *          | block_item_list '}'
     * @return
     */
    private int left31() {
        if (token.tag == Tag.RIGHT_BRACES) {

        } else {

        }
        return 0;
    }

    /**
     * block_item_list ->  block_item rest25
     * @return
     */
    private int block_item_list() {
        return 0;
    }

    /**
     * rest25->  block_item rest25
     *         | epsilon
     * @return
     */
    private int rest25() {
        return 0;
    }

    /**
     * block_item ->  declaration
     *              | statement
     * @return
     */
    private int block_item() {
        return 0;
    }

    /**
     * expression_statement ->  ';'
     *                        | expression ';'
     * @return
     */
    private int expression_statement() {
        if (token.tag == Tag.SEMICOLON) {

        } else {

        }
        return 0;
    }

    /**
     * selection_statement ->  IF '(' expression ')' statement left32
     *                       | SWITCH '(' expression ')' statement
     * @return
     */
    private int selection_statement() {
        switch (token.tag) {
            case Tag.IF:
                break;
            case Tag.SWITCH:
                break;
            default:
                break;
        }
        return 0;
    }

    /**
     * left32 ->  ELSE statement
     *          | epsilon
     * @return
     */
    private int left32() {
        if (token.tag == Tag.ELSE) {

        } else {
            //epsilon
        }
        return 0;
    }

    /**
     * iteration_statement ->  WHILE '(' expression ')' statement
     *                       | DO statement WHILE WHILE '(' espression ')' ';'
     *                       | FOR '(' left33
     * @return
     */
    private int iteration_statement() {
        switch (token.tag) {
            case Tag.WHILE:
                break;
            case Tag.DO:
                break;
            case Tag.FOR:
                break;
            default:
                break;
        }
        return 0;
    }

    /**
     * left33 ->  expression_statement expression_statement left34
     *          | declaration expression_statement left34
     * @return
     */
    private int left33() {
        return 0;
    }

    /**
     * left34 ->  ')' statement
     *          | expression ')' statement
     * @return
     */
    private int left34() {
        if (token.tag == Tag.RIGHT_BRACKETS) {

        } else {

        }
        return 0;
    }

    /**
     * jump_statement ->  GOTO IDENTIFIER ';'
     *                  | CONTINUE ';'
     *                  | BREAK ';'
     *                  | RETURN left35
     * @return
     */
    private int jump_statement() {
        switch (token.tag) {
            case Tag.GOTO:
                break;
            case Tag.CONTINUE:
                break;
            case Tag.BREAK:
                break;
            case Tag.RETURN:
                break;
            default:
                break;
        }
        return 0;
    }

    /**
     * left35 ->  ';'
     *          | expression ';'
     * @return
     */
    private int left35() {
        if (token.tag == Tag.SEMICOLON) {

        } else {

        }
        return 0;
    }

    /**
     * translation_unit -> external_declaration rest26
     * @return
     */
    private int translation_unit() {
        return 0;
    }

    /**
     * rest26 ->  external_declaration rest26
     *          | epsilon
     * @return
     */
    private int rest26() {
        return 0;
    }

    /**
     * external_declaration ->  function_definition
     *                        | declaration
     * @return
     */
    private int external_declaration() {
        return 0;
    }

    /**
     * function_definition -> declaration_specifiers declarator left36
     * @return
     */
    private int function_definition() {
        return 0;
    }

    /**
     * left36 ->  declaration_list compound_statement
     *          | compound_statement
     * @return
     */
    private int left36() {
        return 0;
    }

    /**
     * declaration_list -> declaration rest27
     * @return
     */
    private int declaration_list() {
        return 0;
    }

    /**
     * rest27 ->  declaration rest27
     *          | epsilon
     * @return
     */
    private int rest27() {
        return 0;
    }
}