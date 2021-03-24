package Backend.Controller;

/**
 * Trieda pre naplnenie tabuľiek v GUI.
 *
 * @author Ivan Vykopal
 */
public class TableRecord {
    /** Atribút number predstavuje číselnú hodnotu, počtu chýb. **/
    private int number;

    /** Atribút message predstavuje chybovú správu. **/
    private String message;

    /** Atribút code predstavuje kód chyby. **/
    private String code;

    /**
     * Konštruktor pre inicializáciu atribútov.
     *
     * @param count počet výskytov danej chyby
     *
     * @param message chybová správa
     *
     * @param code kód chyby
     */
    public TableRecord(int count, String message, String code) {
        this.number = count;
        this.message = message;
        this.code = code;
    }

    /**
     * Metóda na zistenie počtu chýb.
     *
     * @return počet chýb
     */
    public int getNumber() {
        return number;
    }

    /**
     * Metóda na nastavenie početu chýb.
     *
     * @param number počet chýb
     */
    public void setNumber(int number) {
        this.number = number;
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
     * @return kód chyby.
     */
    public String getCode() {
        return code;
    }

    /**
     * Metóda na nastvanie kódu chyby.
     *
     * @param code kód chyby.
     */
    public void setCode(String code) {
        this.code = code;
    }

}
