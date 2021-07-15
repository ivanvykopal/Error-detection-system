package Compiler.Lexer;

import Backend.InternationalizationClass;
import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Preprocessing.Preprocessor;
import java.io.*;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Trieda, ktorá spracováva vstup a mení ho na tokeny.
 *
 * <p> Trieda predstavuje lexikálnu analýzu.
 *
 * @author Ivan Vykopal
 */
public final class Scanner {
    /** Atribút file predstavuje analyzovaný súbor v textovej podobe. **/
    private String file;

    /**
     * Atribút keywords predstavuje hash tabuľku kľúčových slov, kde kľúč je názov kľúčoveho slova a hodnotou
     * je číselná reprezentácia kľúčoveho slova.
     **/
    private HashMap<String, Byte> keywords;

    /** Atribút peek predtsavuje práve spracovávaný znak. **/
    private char peek = ' ';

    /** Atribút position predstavuje pozíciu aktuálne spracovávaného znaku vo file. **/
    private int position = 0;

    /** Atribút errorDatabase predstavuje databázu chýb. **/
    private ErrorDatabase errorDatabase;

    /** Atribút line predstavuje aktuálny riadok v súbore. **/
    public static int line = 1;

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

    /**
     * Konštruktor, ktorý načíta vstupný súbor a zároveň naplní HashMap kľúčovými slovami,
     * pre ľahšiu kontrolu.
     *
     * @param file cesta k súboru
     * @param errorDatabase databáza chýb
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
     * Metóda, ktorá spracováva vstup a mení ho na tokeny.
     *
     * @return - vracia token
     */
    public Token scan(){
        while(true) {
            // ignorovanie prázdnych znakov a komentárov
            ignoreWhiteSpaces();
            if (ignoreComments()) {
                errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-03"), "E-LA-03");
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
                        errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-01"), "E-LA-01");
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
            if (flag && word.toString().equals("size_t")) {
                flag = false;
            }
            if (flag) {
                if (word.toString().length() > 31) {
                    errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-02"), "E-LA-02");
                    return new Token((byte) -1, "", line);
                } else {
                    return resolveIdentifier(word.toString());
                }
            } else {
                // vyhľadanie kľúčového slova a jeho hodnoty v HashMape keywords
                Byte tag = keywords.get(word.toString());
                if (tag != null) {
                    //keyword
                    return new Token(tag, word.toString(), line);
                } else {
                    //identifier
                    if (word.toString().length() > 31) {
                        errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-02"), "E-LA-02");
                        return new Token((byte) -1, "", line);
                    } else {
                        return resolveIdentifier(word.toString());
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
            if (word.toString().length() > 31) {
                errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-02"), "E-LA-02");
                return new Token((byte) -1, "", line);
            } else {
                return resolveIdentifier(word.toString());
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
                    errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-04"), "E-LA-04");
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
            boolean hexa = false;
            boolean flag = false;       // flag obsahuje informáciu, či číslo obsahuje x, u alebo l
            boolean real = false;
            StringBuilder word = new StringBuilder("" + peek);
            readNextCharacter();
            while (true) {
                while (Character.isDigit(peek)) {
                    word.append(peek);
                    readNextCharacter();
                }
                if (hexa) {
                    if ((peek >= 65 && peek <= 70) || (peek >= 97 && peek <= 102)) {
                        word.append(peek);
                        readNextCharacter();
                        continue;
                    }
                }
                if (!hexa && (peek == 'e' || peek == 'E')) {
                    word.append(peek);
                    readNextCharacter();
                    real = true;
                    continue;
                }
                if (real) {
                    if (peek == '-' || peek == '+') {
                        word.append(peek);
                        readNextCharacter();
                        continue;
                    }
                }
                if (peek == 'x' || peek == 'X') {
                    word.append(peek);
                    readNextCharacter();
                    hexa = true;
                    continue;
                }
                if (peek == 'u' || peek == 'U' || peek == 'l' || peek == 'L') {
                    word.append(peek);
                    readNextCharacter();
                    flag = true;
                } else {
                    break;
                }
            }
            if (peek != '.') {
                getPreviousPosition();
                if (real) {
                    return new Token(Tag.REAL, word.toString(), line);
                } else {
                    return new Token(Tag.NUMBER, word.toString(), line);
                }
            } else {
                if (flag) {
                    while (peek != ' ') {
                        readNextCharacter();
                    }
                    errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-01"), "E-LA-01");
                    return new Token((byte) -1, "", line);
                }
                flag = false;
                hexa = false;
                while (true) {
                    do {
                        if (peek == ';' || peek == ')') {
                            break;
                        }
                        word.append(peek);
                        readNextCharacter();
                    } while (Character.isDigit(peek));

                    if (hexa) {
                        if ((peek >= 65 && peek <= 70) || (peek >= 97 && peek <= 102)) {
                            word.append(peek);
                            readNextCharacter();
                            continue;
                        }
                    }
                    if (peek == 'e' || peek == 'E') {
                        word.append(peek);
                        readNextCharacter();
                        flag = true;
                        continue;
                    }
                    if (peek == 'x' || peek == 'X') {
                        word.append(peek);
                        readNextCharacter();
                        hexa = true;
                        continue;
                    }
                    if (flag && (peek == '-' || peek == 'l' || peek == 'L')) {
                        word.append(peek);
                        readNextCharacter();
                    } else {
                        break;
                    }
                }
                getPreviousPosition();
                return new Token(Tag.REAL, word.toString(), line);
            }
        }


        if (peek == '§') {
            return new Token(Tag.EOF,"", line);
        }
        errorDatabase.addErrorMessage(line, InternationalizationClass.getErrors().getString("E-LA-01"), "E-LA-01");
        return new Token((byte) -1, "", line);
    }

    /**
     * Metóda na načítanie ďalšieho znaku.
     */
    private void readNextCharacter() {
        // načítanie ďalšieho znaku
        if (position < file.length()) {
            peek = file.charAt(position++);
        } else {
            // ukončovací znak
            peek = '§';
        }
    }

    /**
     * Metóda, ktorá vyráta predchádzajúcu pozíciu.
     */
    private void getPreviousPosition() {
        position--;
    }

    /**
     * Metóda, ktorá ignoruje a prechádza cez prázdne znaky (biele znaky).
     */
    private void ignoreWhiteSpaces() {
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
     * Metóda, ktorá ignoruje komentáre.
     *
     * @return true, ak je koniec súboru, inak false
     */
    private boolean ignoreComments() {
        if (peek == '/') {
            readNextCharacter();
            switch (peek) {
                case '*':
                    // komentár typu /* */
                    readNextCharacter();
                    while(true) {
                        if (peek == '\n') {
                            line++;
                        }
                        if (peek == '§') {
                            // koniec súboru
                            return true;
                        }
                        if (peek == '*') {
                            readNextCharacter();
                            if (peek == '/') {
                                readNextCharacter();
                                break;
                            }
                        } else {
                            readNextCharacter();
                        }
                    }
                    break;
                case '/':
                    // komentár typu //
                    while (true) {
                        readNextCharacter();
                        if (peek == '\n') {
                            //line++;
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
     * Metóda, ktorá spracováva identifikátory. Určité identifikátory konvertuje na iné.
     *
     * @param identifier identifikátor
     *
     * @return token
     */
    private Token resolveIdentifier(String identifier) {
        switch(identifier) {
            case "true":
                return new Token(Tag.NUMBER, "1", line);
            case "false":
                return new Token(Tag.NUMBER, "0", line);
        }
        java.util.Scanner scanner;
        try {
            File file = new File("config/types.config");
            scanner = new java.util.Scanner(file);
        } catch (FileNotFoundException e) {
            InputStream is = getClass().getResourceAsStream("/config/types.config");
            scanner = new java.util.Scanner(is);
        }
        try {
            while (scanner.hasNextLine()) {
                String configLine = scanner.nextLine();
                if (configLine.contains(identifier+"=")) {
                    String[] words = configLine.split("=");
                    if (!words[0].trim().equals(identifier)) {
                        continue;
                    }
                    if (words.length == 2) {
                        return new Token(Tag.INT, words[1].trim(), line);
                    }
                }
            }
            return new Token(Tag.IDENTIFIER, identifier, line);
        } catch (Exception e) {
            ProgramLogger.createLogger(Scanner.class.getName()).log(Level.WARNING,
                    bundle.getString("configErr1"));
        }
        return new Token(Tag.IDENTIFIER, identifier, line);
    }

    /**
     * Metóda, ktorá pridá klúčové slová do HashMap-y.
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

        keywords.put("size_t", Tag.SIZE_T);
        keywords.put("FILE", Tag.FILE);
    }
}