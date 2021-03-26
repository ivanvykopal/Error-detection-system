package Backend.Controller;

import java.math.BigDecimal;

/**
 * Trieda pre naplnenie celkovej tabuľky v GUI pri štatistikách.
 *
 * @author Ivan Vykopal
 */
public class SummaryTableRecord {
    /**
     * Atribút record1 predstavuje informácie o počte výskytov danej chyby spolu s percentuálnym podielom z celkového
     * počtu chýb.
     **/
    private String record1;

    /** Atribút message predstavuje chybovú správu. **/
    private String message;

    /** Atribút code predstavuje kód chyby. **/
    private String code;

    /**
     * Atribút record2 predstavuje informácie o počte súborov, v ktorých sa chyby nachádza spolu s percentuálnym
     * podielom počtu súborov s danou chybou.
     **/
    private String record2;

    /**
     * Konštruktor pre inicializáciu atribútov.
     *
     * @param record1 počet výskytov danej chyby
     *
     * @param message chybová správa
     *
     * @param code kód chyby
     *
     * @param record2 precentuálny podiel chyby
     */
    public SummaryTableRecord(String record1, String message, String code, String record2) {
        this.record1 = record1;
        this.message = message;
        this.code = code;
        this.record2 = record2;
    }

    /**
     * Metóda na zistenie počtu chýb.
     *
     * @return počet chýb
     */
    public String getRecord1() {
        return record1;
    }

    /**
     * Metóda na nastavenie početu chýb.
     *
     * @param record1 počet chýb
     */
    public void setRecord1(String record1) {
        this.record1 = record1;
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
    public String getRecord2() {
        return record2;
    }

    /**
     * Metóda na nstavenie percentuálneho podielu chyby.
     *
     * @param record2 percentuálny podiel chyby
     */
    public void setRecord2(String record2) {
        this.record2 = record2;
    }
}
