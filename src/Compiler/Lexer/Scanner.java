package Compiler.Lexer;

import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.Preprocessing.Preprocessor;

import java.io.IOException;
import java.util.HashMap;

/**
 * Trieda, ktorá spracováva vstup a mení ho na tokeny.
 */
public class Scanner {
    private String file;
    private HashMap<String, Byte> keywords;
    private char peek = ' ';
    private int position = 0;
    private Error err = new Error();
    private ErrorDatabase errorDatabase;

    public static int line = 1;

    /**
     * Konštruktor, ktorý načíta vstupný súbor a zároveň naplní HashMap kľúčovými slovami,
     * pre ľahšiu kontrolu.
     * @param file - cesta k súboru
     */
    public Scanner(String file, ErrorDatabase errorDatabase) {
        this.errorDatabase = errorDatabase;
        position = 0;
        line = 1;
        peek = ' ';
        keywords = new HashMap<>();
        Preprocessor prep = new Preprocessor(file);
        this.file = prep.preprocess();

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
            if (ignoreComments()) {
                //TODO: Chýbajúca časť uzavretia
                System.out.println("Chyba: E-LA-03 " + err.getError("E-LA-03"));
                errorDatabase.addErrorMessage(line, err.getError("E-LA-03"), "E-LA-03");
                return new Token((byte) -1, "", line);
            }
            if (peek == '\n') {
                line++;
                continue;
            }
            // ak sú ešte prázdne znaky opakuje cyklus
            if(peek == ' ' || peek == '\t' || peek == '\r') continue;
            if (peek == '/') {
                readNextCharacter();
                //System.out.println("SOM TU");
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
                    return new Token(Tag.ASSIGNMENT, "/=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.DIV, "/", line);
                }
            case '+': // + ++ +=
                readNextCharacter();
                if (peek == '+') {
                    return new Token(Tag.INC, "++", line);
                } else if(peek == '=') {
                    return new Token(Tag.ASSIGNMENT, "+=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.PLUS, "+", line);
                }
            case '-': // - -- -= ->
                readNextCharacter();
                if(peek == '-') {
                    return new Token(Tag.DEC, "--", line);
                } else if (peek == '=') {
                    return new Token(Tag.ASSIGNMENT, "-=", line);
                } else if (peek == '>') {
                    return new Token(Tag.ARROW, "->", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.MINUS, "-", line);
                }
            case '*': // * *=, *
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.ASSIGNMENT, "*=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.MULT, "*", line);
                }
            case '%': // % %=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.ASSIGNMENT, "%=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.MOD, "%", line);
                }
            case '=': // = ==
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.EQ, "==", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.ASSIGNMENT, "=", line);
                }
            case '!': // != !
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.NOT_EQ, "!=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.LOGICAL_NOT, "!", line);
                }
            case '>': // > >= >> >>=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.GEQT, ">=", line);
                } else if(peek == '>') {
                    readNextCharacter();
                    if (peek == '=') {
                        return new Token(Tag.ASSIGNMENT, ">>=", line);
                    } else {
                        getPreviousPosition();
                        return new Token(Tag.RIGHT_SHIFT, ">>", line);
                    }
                } else {
                    getPreviousPosition();
                    return new Token(Tag.GT, ">", line);
                }
            case '<': // < <= << <<=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.LEQT, "<=", line);
                } else if (peek == '<') {
                    readNextCharacter();
                    if (peek == '=') {
                        return new Token(Tag.ASSIGNMENT, "<<=", line);
                    } else {
                        getPreviousPosition();
                        return new Token(Tag.LEFT_SHIFT, "<<", line);
                    }
                } else {
                    getPreviousPosition();
                    return new Token(Tag.LT, "<", line);
                }
            case '&': // && & &= , &
                readNextCharacter();
                if (peek == '&') {
                    return new Token(Tag.LOGICAL_AND, "&&", line);
                } else if (peek == '=') {
                    return new Token(Tag.ASSIGNMENT, "&=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.AND, "&", line);
                }
            case '|': // || | |=
                readNextCharacter();
                if (peek == '|') {
                    return new Token(Tag.LOGICAL_OR, "||", line);
                } else if (peek == '=') {
                    return new Token(Tag.ASSIGNMENT, "|=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.OR, "|", line);
                }
            case '~':
                return new Token(Tag.BITWISE_NOT, "~", line);
            case '^': // ^ ^=
                readNextCharacter();
                if (peek == '=') {
                    return new Token(Tag.ASSIGNMENT, "^=", line);
                } else {
                    getPreviousPosition();
                    return new Token(Tag.XOR, "^", line);
                }
            case '?':
                return new Token(Tag.QMARK, "?", line);
            case '[':
                return new Token(Tag.LEFT_BRACKETS, "[", line);
            case ']':
                return new Token(Tag.RIGHT_BRACKETS, "]", line);
            case '(':
                return new Token(Tag.LEFT_PARENTHESES, "(", line);
            case ')':
                return new Token(Tag.RIGHT_PARENTHESES, ")", line);
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
                readNextCharacter();
                if (peek == '.') {
                    readNextCharacter();
                    if (peek == '.') {
                        return new Token(Tag.ELLIPSIS, "...", line);
                    } else {
                        //TODO: chyba, nevedel zaradiť token
                        System.out.println("Chyba: E-LA-01 " + err.getError("E-LA-01"));
                        errorDatabase.addErrorMessage(line, err.getError("E-LA-01"), "E-LA-01");
                        return new Token((byte) -1, "", line);
                    }
                } else {
                    getPreviousPosition();
                    return new Token(Tag.DOT, ".", line);
                }
        }

        if(Character.isLetter(peek)) {
            // flag vraví o tom, či reťazec obsahuje _
            boolean flag = false;
            StringBuilder word = new StringBuilder("" + peek);
            while (true) {
                readNextCharacter();
                if(Character.isLetterOrDigit(peek)) {
                    word.append(peek);
                    continue;
                }
                if (peek == '_') {
                    word.append(peek);
                    flag = true;
                } else {
                    getPreviousPosition();
                    break;
                }
            }
            if (flag) {
                //TODO: prekročenie dĺžky názvu identifikátoru
                if (word.toString().length() > 31) {
                    System.out.println("Chyba: E-LA-02 " + err.getError("E-LA-02"));
                    errorDatabase.addErrorMessage(line, err.getError("E-LA-02"), "E-LA-02");
                    return new Token((byte) -1, "", line);
                } else {
                    return new Token(Tag.IDENTIFIER, word.toString(), line);
                }
            } else {
                // vyhľadanie kľúčového slova a jeho hodnoty v HashMape keywords
                Byte tag = keywords.get(word.toString());
                if (tag != null) {
                    //keyword
                    return new Token(tag, word.toString(), line);
                } else {
                    //identifier
                    //TODO: prekročenie dĺžky názvu identifikátoru
                    if (word.toString().length() > 31) {
                        System.out.println("Chyba: E-LA-02 " + err.getError("E-LA-02"));
                        errorDatabase.addErrorMessage(line, err.getError("E-LA-02"), "E-LA-02");
                        return new Token((byte) -1, "", line);
                    } else {
                        return new Token(Tag.IDENTIFIER, word.toString(), line);
                    }
                }
            }
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
            //TODO: prekročenie dĺžky názvu identifikátoru
            if (word.toString().length() > 31) {
                System.out.println("Chyba: E-LA-02 " + err.getError("E-LA-02"));
                errorDatabase.addErrorMessage(line, err.getError("E-LA-02"), "E-LA-02");
                return new Token((byte) -1, "", line);
            } else {
                return new Token(Tag.IDENTIFIER, word.toString(), line);
            }
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
                } else if (peek == '\n'){
                    //TODO: Chýbajúce úvodzovky pre reťazcovú konštantu
                    System.out.println("Chyba: E-LA-04 " + err.getError("E-LA-04"));
                    errorDatabase.addErrorMessage(line, err.getError("E-LA-04"), "E-LA-04");
                    return new Token((byte) -1, "", line);
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
            if (peek == '\'') {
                System.out.print("Chyba pre znak!");
                return new Token((byte) -1, "", line);
            }
            if (peek == '\\') {
                word.append(peek);
                readNextCharacter();
                word.append(peek);
            } else {
                word.append(peek);
            }
            readNextCharacter();
            if (peek == '\'') {
                word.append(peek);
                return new Token(Tag.CHARACTER, word.toString(), line);
            } else {
                System.out.print("Chyba pre znak!");
                return new Token((byte) -1, "", line);
            }
        }

        // čísla
        if(Character.isDigit(peek)) {
            StringBuilder word = new StringBuilder("" + peek);
            readNextCharacter();
            while (Character.isDigit(peek)) {
                word.append(peek);
                readNextCharacter();
            }
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


        if (peek == '§') {
            return new Token(Tag.EOF,"", line);
        }
        //TODO: chyba, nevedel zaradiť token
        System.out.println("Chyba: E-LA-01 " + err.getError("E-LA-01"));
        errorDatabase.addErrorMessage(line, err.getError("E-LA-01"), "E-LA-01");
        return new Token((byte) -1, "", line);
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
            if (peek == '\r') continue;
            // kontrola nového riadku
            if (peek == '\n') line++;
            else break;
        }
    }

    /**
     * Funkcia, ktorá ignoruje komentáre.
     * @throws IOException
     */
    private boolean ignoreComments() throws IOException {
        if (peek == '/') {
            readNextCharacter();
            switch (peek) {
                case '*':
                    // komentár typu /* */
                    while(true) {
                        readNextCharacter();
                        if (peek == '*' && readNextCharacter('/')) {
                            readNextCharacter();
                            break;
                        }
                        if (peek == '§') {
                            // koniec súboru
                            return true;
                        }
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
        return false;
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
        keywords.put("register", Tag.REGISTER);
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