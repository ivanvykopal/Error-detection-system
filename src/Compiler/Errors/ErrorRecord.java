package Compiler.Errors;

/**
 *
 */
public class ErrorRecord {

    private int line;
    private String message;
    private String code;

    /**
     *
     * @param line
     * @param message
     * @param code
     */
    public ErrorRecord(int line, String message, String code) {
        this.line = line;
        this.message = message;
        this.code = code;
    }

    /**
     *
     * @return
     */
    public int getLine() {
        return line;
    }

    /**
     *
     * @param line
     */
    public void setLine(int line) {
        this.line = line;
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

    @Override
    public String toString() {
        return "Chyba: " + code + " " + message + " na riadku " + line + "!";
    }
}
