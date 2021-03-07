package Compiler.Errors;

import java.util.HashMap;

/**
 *
 */
public class ErrorDatabase implements Cloneable {

    private HashMap<Integer, ErrorRecord> errorTable = new HashMap<>();

    /**
     *
     * @param line
     * @param message
     * @param code
     */
    public void addErrorMessage(int line, String message, String code) {
        ErrorRecord newRecord = new ErrorRecord(line, message, code);
        errorTable.putIfAbsent(line, newRecord);
    }

    public void getErrorMessages() {
        for (int index: errorTable.keySet()) {
            ErrorRecord record = errorTable.get(index);
            System.out.println(record.toString());
        }
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

}