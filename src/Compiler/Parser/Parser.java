package Compiler.Parser;

import Compiler.Lexer.Token;

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
}
