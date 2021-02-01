package Compiler.Parser;

import Compiler.Lexer.Token;
import com.sun.org.apache.bcel.internal.generic.ARETURN;
import javafx.fxml.FXML;

public class Parser {
    private Token token;

    public Parser() {

    }

    private void nextToken() {

    }

    private boolean accept(byte tag) {
        if (token.tag == tag) {
            nextToken();
            return true;
        }
        return false;
    }

    private int primary_expression() {
        return 0;
    }

    private int constant() {
        return 0;
    }

    private int enumeration_constant() {
        return 0;
    }

    private int postfix_expression() {
        return 0;
    }

    private int rest1() {
        return 0;
    }

    private int left1() {
        return 0;
    }

    private int left2() {
        return 0;
    }

    private int argument_expression_list() {
        return 0;
    }

    private int rest2() {
        return 0;
    }

    private int unary_expression() {
        return 0;
    }

    private int left3() {
        return 0;
    }

    private int unary_operator() {
        return 0;
    }

    private int cast_expression() {
        return 0;
    }

    private int multiplicative_expression() {
        return 0;
    }

    private int rest3() {
        return 0;
    }

    private int additive_expression() {
        return 0;
    }

    private int rest4() {
        return 0;
    }

    private int shift_expression() {
        return 0;
    }

    private int rest5() {
        return 0;
    }

    private int relational_expression() {
        return 0;
    }

    private int rest6() {
        return 0;
    }

    private int equality_expression() {
        return 0;
    }

    private int rest7() {
        return 0;
    }

    private int and_expression() {
        return 0;
    }

    private int rest8() {
        return 0;
    }

    private int exclusive_or_expression() {
        return 0;
    }

    private int rest9() {
        return 0;
    }

    private int inclusive_or_expression() {
        return 0;
    }

    private int rest10() {
        return 0;
    }

    private int logical_and_expression() {
        return 0;
    }

    private int rest11() {
        return 0;
    }

    private int logical_or_expression() {
        return 0;
    }

    private int rest12() {
        return 0;
    }

    private int conditional() {
        return 0;
    }

    private int left4() {
        return 0;
    }

    private int assignment_expression() {
        return 0;
    }

    private int assignment_operator() {
        return 0;
    }

    private int expression() {
        return 0;
    }

    private int rest13() {
        return 0;
    }

    private int constant_expression() {
        return 0;
    }

    private int declaration() {
        return 0;
    }

    private int left5() {
        return 0;
    }

    private int declaration_specifiers() {
        return 0;
    }

    private int left6() {
        return 0;
    }

    private int init_declarator_list() {
        return 0;
    }

    private int rest14() {
        return 0;
    }

    private int init_declarator() {
        return 0;
    }

    private int left7() {
        return 0;
    }

    private int storage_class_specifier() {
        return 0;
    }

    private int type_specifier() {
        return 0;
    }

    private int struct_or_union_specifier() {
        return 0;
    }

    private int left8() {
        return 0;
    }

    private int left9() {
        return 0;
    }

    private int struct_or_union() {
        return 0;
    }

    private int struct_declaration_list() {
        return 0;
    }

    private int rest15() {
        return 0;
    }

    private int struct_declaration() {
        return 0;
    }

    private int left10() {
        return 0;
    }

    private int specifier_qualifier_list() {
        return 0;
    }

    private int left11() {
        return 0;
    }

    private int struct_declarator_list() {
        return 0;
    }

    private int rest16() {
        return 0;
    }

    private int struct_declarator() {
        return 0;
    }

    private int left12() {
        return 0;
    }

    private int enum_specifier() {
        return 0;
    }

    private int left13() {
        return 0;
    }

    private int left14() {
        return 0;
    }

    private int enumerator_list() {
        return 0;
    }

    private int rest17() {
        return 0;
    }

    private int enumerator() {
        return 0;
    }

    private int left15() {
        return 0;
    }

    private int type_qualifier() {
        return 0;
    }

    private int declarator() {
        return 0;
    }

    private int direct_declarator() {
        return 0;
    }

    private int rest18() {
        return 0;
    }

    private int left16() {
        return 0;
    }

    private int left17() {
        return 0;
    }

    private int left18() {
        return 0;
    }

    private int left19() {
        return 0;
    }

    private int pointer() {
        return 0;
    }

    private int left20() {
        return 0;
    }

    private int left21() {
        return 0;
    }

    private int type_qualifier_list() {
        return 0;
    }

    private int rest19() {
        return 0;
    }

    //TODO: pozrieť sa na to ??
    private int parameter_type_list() {
        return 0;
    }

    private int parameter_list() {
        return 0;
    }

    private int rest20() {
        return 0;
    }

    private int parameter_declaration() {
        return 0;
    }

    private int left22() {
        return 0;
    }

    private int identifier_list() {
        return 0;
    }

    private int rest21() {
        return 0;
    }

    private int type_name() {
        return 0;
    }

    private int left23() {
        return 0;
    }

    private int abstract_declarator() {
        return 0;
    }

    private int left24() {
        return 0;
    }

    private int direct_abstract_declarator() {
        return 0;
    }

    private int left25() {
        return 0;
    }

    private int left26() {
        return 0;
    }

    private int left27() {
        return 0;
    }

    private int left28() {
        return 0;
    }

    private int rest22() {
        return 0;
    }

    private int left29() {
        return 0;
    }

    private int initialzer() {
        return 0;
    }

    private int initializer_list() {
        return 0;
    }

    private int rest23() {
        return 0;
    }

    private int left30() {
        return 0;
    }

    private int designation() {
        return 0;
    }

    private int designator_list() {
        return 0;
    }

    private int rest24() {
        return 0;
    }

    private int designator() {
        return 0;
    }

    private int statement() {
        return 0;
    }

    private int labeled_statement() {
        return 0;
    }

    private int compound_statement() {
        return 0;
    }

    private int left31() {
        return 0;
    }

    private int block_item_list() {
        return 0;
    }

    private int rest25() {
        return 0;
    }

    private int block_item() {
        return 0;
    }

    private int expression_statement() {
        return 0;
    }

    private int selection_statement() {
        return 0;
    }

    private int left32() {
        return 0;
    }

    private int iteration_statement() {
        return 0;
    }

    private int left33() {
        return 0;
    }

    private int left34() {
        return 0;
    }

    private int jump_statement() {
        return 0;
    }

    private int left35() {
        return 0;
    }

    private int translation_unit() {
        return 0;
    }

    private int rest26() {
        return 0;
    }

    private int external_declaration() {
        return 0;
    }

    private int function_definition() {
        return 0;
    }

    private int left36() {
        return 0;
    }

    private int declaration_list() {
        return 0;
    }

    private int rest27() {
        return 0;
    }

}
