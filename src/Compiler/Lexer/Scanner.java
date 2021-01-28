package Compiler.Lexer;

import java.io.IOException;
import java.util.HashMap;

/**
 * Trieda, ktorá spracováva vstup a mení ho na tokeny.
 */
public class Scanner {
    private String file;
    private HashMap<String, Byte> keywords = new HashMap<>();
    private char peek = ' ';
    private int position = 0;

    public static int line = 1;

    /**
     * Konštruktor, ktorý načíta vstupný súbor a zároveň naplní HashMap kľúčovými slovami,
     * pre ľahšiu kontrolu.
     * @param file - cesta k súboru
     */
    public Scanner(String file) {
        this.file = file;

        addKeywords();
    }

    /**
     * Funkcia, ktorá spracováva vstup mení ho na tokeny.
     * @return - vracia token
     * @throws IOException
     */
    public Token scan() throws IOException {
        while(true) {
            // ignorovanie prázdnych znakov a komentárov
            ignoreWhiteSpaces();
            ignoreComments();
            if (peek == '\n') {
                line++;
                continue;
            }
            // ak sú ešte prázdne znaky opakuje cyklus
            if(peek == ' ' || peek == '\t') continue;
            if (peek == '/') {
                readNextCharacter();
                // ak nasleduje komentár opakuje cyklus
                if (peek == '/' || peek == '*') {
                    position -= 2;
                    readNextCharacter();
                } else {
                    position -= 2;
                    readNextCharacter();
                    break;
                }
            }
            else break;
        }

        switch (peek) {
            case '/': // / /=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "/=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_ARITHMETIC, "/", line);
                }
            case '+': // + ++ +=
                readNextCharacter();
                if (peek == '+') {
                    return new Token(Tag.OP_ARITHMETIC, "++", line);
                } else if(peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "+=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_ARITHMETIC, "+", line);
                }
            case '-': // - -- -= ->
                readNextCharacter();
                if(peek == '-') {
                    return new Token(Tag.OP_ARITHMETIC, "--", line);
                } else if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "-=", line);
                } else if (peek == '>') {
                    return new Token(Tag.ARROW, "->", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_ARITHMETIC, "-", line);
                }
            case '*': // * *=, *
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "*=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_ARITHMETIC, "*", line);
                }
                //TODO: prípad, ak ide o smerník *
            case '%': // % %=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "%=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_ARITHMETIC, "%", line);
                }
            case '=': // = ==
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "==", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_ASSIGNMENT, "=", line);
                }
            case '!': // != !
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_RELATIONAL, "!=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_LOGICAL, "!", line);
                }
            case '>': // > >= >> >>=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_RELATIONAL, ">=", line);
                } else if(peek == '>') {
                    readNextCharacter();
                    if (peek == '=') {
                        return new Token(Tag.OP_ASSIGNMENT, ">>=", line);
                    } else {
                        getPreviousPosition();
                        return new Token(Tag.OP_BITWISE, ">>", line);
                    }
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_RELATIONAL, ">", line);
                }
            case '<': // < <= << <<=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_RELATIONAL, "<=", line);
                } else if (peek == '<') {
                    readNextCharacter();
                    if (peek == '=') {
                        return new Token(Tag.OP_ASSIGNMENT, "<<=", line);
                    } else {
                        getPreviousPosition();
                        return new Token(Tag.OP_BITWISE, "<<", line);
                    }
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_RELATIONAL, "<", line);
                }
            case '&': // && & &= , &
                readNextCharacter();
                if (peek == '&') {
                    return new Token(Tag.OP_LOGICAL, "&&", line);
                } else if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "&=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_BITWISE, "&", line);
                }
                //TODO: prípad, ak ide o smerník &
            case '|': // || | |=
                readNextCharacter();
                if (peek == '|') {
                    return new Token(Tag.OP_LOGICAL, "||", line);
                } else if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "|=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_BITWISE, "|", line);
                }
            case '~':
                return new Token(Tag.OP_BITWISE, "~", line);
            case '^': // ^ ^=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.OP_ASSIGNMENT, "^=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OP_BITWISE, "^", line);
                }
            case '?':
                return new Token(Tag.QMARK, "?", line);
            case '[':
                return new Token(Tag.LEFT_PARENTHESES, "[", line);
            case ']':
                return new Token(Tag.RIGHT_PARENTHESES, "]", line);
            case '(':
                return new Token(Tag.LEFT_BRACKETS, "(", line);
            case ')':
                return new Token(Tag.RIGHT_BRACKETS, ")", line);
            case '{':
                return new Token(Tag.LEFT_BRACES, "{", line);
            case '}':
                return new Token(Tag.RIGHT_BRACES, "}", line);
            case ',':
                return new Token(Tag.COMMA, ",", line);
            case ';':
                return new Token(Tag.SEMICOLON, ";", line);
            case ':':
                return new Token(Tag.COLON, ":", line);
            case '#':
                return new Token(Tag.HASHTAG, "#", line);
            case '.':
                return new Token(Tag.DOT, ".", line);
        }

        //identifikátor začinajúci s _
        if (peek == '_') {
            StringBuilder word = new StringBuilder("" + peek);
            while (true) {
                readNextCharacter();
                if (Character.isLetterOrDigit(peek) || peek == '_') {
                    word.append(peek);
                    continue;
                }
                getPreviousPosition();
                break;
            }
            return new Token(Tag.IDENTIFIER, word.toString(), line);
        }

        // stringy -> reťazce s ""
        if (peek == '"') {
            StringBuilder word = new StringBuilder("" + peek);
            while(true) {
                readNextCharacter();
                if (peek == '\\') {
                    word.append(peek);
                    readNextCharacter();
                    word.append(peek);
                } else if (peek == '"') {
                    word.append(peek);
                    break;
                } else {
                    word.append(peek);
                }
            }
            return new Token(Tag.STRING, word.toString(), line);
        }

        // znaky s ''
        if (peek == '\'') {
            StringBuilder word = new StringBuilder("" + peek);
            readNextCharacter();
            if (peek == '\\' || peek == '\'') {
                System.out.print("Chyba pre znak!");
                return null;
            } else {
                word.append(peek);
            }
            readNextCharacter();
            if (peek == '\'') {
                word.append(peek);
                return new Token(Tag.CHARACTER, word.toString(), line);
            } else {
                System.out.print("Chyba pre znak!");
                return null;
            }
        }

        // čísla
        if(Character.isDigit(peek)) {
            StringBuilder word = new StringBuilder("" + peek);
            do {
                readNextCharacter();
                word.append(peek);
            } while (Character.isDigit(peek));
            if (peek != '.') {
                getPreviousPosition();
                return new Token(Tag.NUMBER, word.toString(), line);
            } else {
                do {
                    word.append(peek);
                    readNextCharacter();
                } while (Character.isDigit(peek));
                getPreviousPosition();
                return new Token(Tag.REAL, word.toString(), line);
            }
        }

        //TODO: chyba, nevedel zaradiť token
        System.out.println("Nezaradený token!");
        return null;
    }

    /**
     * Funkcia na načítanie ďalšieho znaku.
     * @throws IOException
     */
    private void readNextCharacter() throws IOException {
        // načítanie ďalšieho znaku
        if (position < file.length()) {
            peek = file.charAt(position++);
        } else {
            // ukončovací znak
            peek = '§';
        }
    }

    /**
     * Funkcia, ktorá načíta nasledujúci znak a porovnáho s parametrom.
     * @param c znak, ktorý má byť nasledujúci
     * @throws IOException
     */
    private boolean readNextCharacter(char c) throws IOException {
        readNextCharacter();

        // kontrola, či je koniec súboru
        if (peek == '§') {
            return false;
        }

        // kontrola, či nasledujúci znak je rovný c
        if (peek == c) {
            return true;
        } else {
            peek = ' ';
            return false;
        }
    }

    /**
     * Funkcia, ktorá vyráta predchádzajúcu pozíciu.
     */
    private void getPreviousPosition() {
        position--;
    }

    /**
     * Funkcia, ktorá ignoruje prechádza cez prázdne znaky.
     * @throws IOException
     */
    private void ignoreWhiteSpaces() throws IOException {
        while(true) {
            readNextCharacter();
            // kontrola konca súboru
            if (peek == '§') break;
            // kontrola pre medzeru a tabulátor
            if (peek == ' ' || peek == '\t') continue;
            // kontrola pre CR znak
            if ((int)peek == 13) continue;
            // kontrola nového riadku
            if (peek == '\n') line++;
            else break;
        }
    }

    /**
     * Funkcia, ktorá ignoruje komentáre.
     * @throws IOException
     */
    private void ignoreComments() throws IOException {
        if (peek == '/') {
            readNextCharacter();
            switch (peek) {
                case '*':
                    // komentár typu /* */
                    while(true) {
                        readNextCharacter();
                        if (peek == '*' && readNextCharacter('/')) break;
                    }
                    break;
                case '/':
                    // komentár typu //
                    while (true) {
                        readNextCharacter();
                        if (peek == '\n') {
                            line++;
                            break;
                        }
                    }
                    break;
                default:
                    // prípad, ak nejde o komentár
                    position -= 2;
                    readNextCharacter();
            }
        }
    }

    /**
     * Funkcia, ktorá pridá klúčové slová do HashMap.
     */
    private void addKeywords() {
        keywords.put("auto", Tag.AUTO);
        keywords.put("break", Tag.BREAK);
        keywords.put("case", Tag.CASE);
        keywords.put("char", Tag.CHAR);
        keywords.put("const", Tag.CONST);
        keywords.put("continue", Tag.CONTINUE);
        keywords.put("default", Tag.DEFAULT);
        keywords.put("do", Tag.DO);
        keywords.put("double", Tag.DOUBLE);
        keywords.put("else", Tag.ELSE);
        keywords.put("enum", Tag.ENUM);
        keywords.put("extern", Tag.EXTERN);
        keywords.put("float", Tag.FLOAT);
        keywords.put("for", Tag.FOR);
        keywords.put("goto", Tag.GOTO);
        keywords.put("if", Tag.IF);
        keywords.put("int", Tag.INT);
        keywords.put("long", Tag.LONG);
        keywords.put("Register", Tag.REGISTER);
        keywords.put("return", Tag.RETURN);
        keywords.put("short", Tag.SHORT);
        keywords.put("signed", Tag.SIGNED);
        keywords.put("sizeof", Tag.SIZEOF);
        keywords.put("static", Tag.STATIC);
        keywords.put("struct", Tag.STRUCT);
        keywords.put("switch", Tag.SWITCH);
        keywords.put("typedef", Tag.TYPEDEF);
        keywords.put("union", Tag.UNION);
        keywords.put("unsigned", Tag.UNSIGNED);
        keywords.put("void", Tag.VOID);
        keywords.put("volatile", Tag.VOLATILE);
        keywords.put("while", Tag.WHILE);
    }
}