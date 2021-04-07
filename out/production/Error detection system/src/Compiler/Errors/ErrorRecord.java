package Compiler.Errors;

/**
 *  Trieda obsahujúca informácie o chybe.
 *
 * @author Ivan Vykopal
 */
public class ErrorRecord {
    /** Atribút line predtsavuje riadok výskytu chyby. **/
    private int line;

    /** Atribút message predstavuje chybovú správu. **/
    private String message;

    /** Atribút code predstavuje kód chyby. **/
    private String code;

    /**
     * Konštruktor, ktorý vytvára triedu {@code ErrorRecord} a inicilizuje jej atribúty.
     *
     * @param line riadok výskytu chyby
     *
     * @param message chybová správa
     *
     * @param code kód chyby
     */
    public ErrorRecord(int line, String message, String code) {
        this.line = line;
        this.message = message;
        this.code = code;
    }

    /**
     * Metóda na zistenie riadku výskytu chyby.
     *
     * @return riadok výskytu chyby
     */
    public int getLine() {
        return line;
    }

    /**
     * Metóda pre nastavenie riadku výskytu chyby.
     *
     * @param line riadok výskytu chyby
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Metóda na zistenie chybovej správy.
     *
     * @return chybová správa
     */
    public String getMessage() {
        return message;
    }

    /**
     * Metóda na nastavenie chybovej správy.
     *
     * @param message chybová správa
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Metóda na zistenie kódu chyby.
     *
     * @return kód chyby
     */
    public String getCode() {
        return code;
    }

    /**
     * Metóda na nastavenie kódu chyby.
     *
     * @param code kód chyby
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Metóda na vrátenie reťazca chyby.
     *
     * @return reťazec chyby
     */
    @Override
    public String toString() {
        return "Chyba: " + code + " " + message + " na riadku " + line + "!";
    }
}
