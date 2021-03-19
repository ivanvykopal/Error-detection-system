package Backend.Controller;

import java.math.BigDecimal;

/**
 *
 */
public class SummaryTableRecord {

    private int number;
    private String message;
    private String code;
    private BigDecimal percent;

    /**
     *
     * @param count
     * @param message
     * @param code
     */
    public SummaryTableRecord(int count, String message, String code, BigDecimal percent) {
        this.number = count;
        this.message = message;
        this.code = code;
        this.percent = percent;
    }

    /**
     *
     * @return
     */
    public int getNumber() {
        return number;
    }

    /**
     *
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }


    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }
}
