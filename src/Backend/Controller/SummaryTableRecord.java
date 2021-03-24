package Backend.Controller;

import java.math.BigDecimal;

/**
 * Trieda pre naplnenie celkovej tabuľky v GUI pri štatistikách.
 *
 * @author Ivan Vykopal
 */
public class SummaryTableRecord {
    /** Atribút number predstavuje číselnú hodnotu, počtu chýb. **/
    private int number;

    /** Atribút message predstavuje chybovú správu. **/
    private String message;

    /** Atribút code predstavuje kód chyby. **/
    private String code;

    /** Atribút percent predstavuje precentuálny podiel chyby. **/
    private BigDecimal percent;

    /**
     * Konštruktor pre inicializáciu atribútov.
     *
     * @param count počet výskytov danej chyby
     *
     * @param message chybová správa
     *
     * @param code kód chyby
     *
     * @param percent precentuálny podiel chyby
     */
    public SummaryTableRecord(int count, String message, String code, BigDecimal percent) {
        this.number = count;
        this.message = message;
        this.code = code;
        this.percent = percent;
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

    /**
     * Metóda na zistenie percentuálneho podielu chyby.
     *
     * @return percentuálny podiel chyby
     */
    public BigDecimal getPercent() {
        return percent;
    }

    /**
     * Metóda na nstavenie percentuálneho podielu chyby.
     *
     * @param percent percentuálny podiel chyby
     */
    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }
}
