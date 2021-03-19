package Backend.Controller;

/**
 *
 */
public class TableRecord {

    private Object number;
    private String message;
    private String code;

    /**
     *
     * @param count
     * @param message
     * @param code
     */
    public TableRecord(Object count, String message, String code) {
        this.number = count;
        this.message = message;
        this.code = code;
    }

    /**
     *
     * @return
     */
    public Object getNumber() {
        return number;
    }

    /**
     *
     * @param number
     */
    public void setNumber(Object number) {
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

}
