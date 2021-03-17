package Compiler.Errors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public ErrorDatabase createCopy() {
        try {
            return (ErrorDatabase) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmpty() {
        return errorTable.isEmpty();
    }

    public void createFile(String sourceFile) {
        try {
            File fileError = new File("errors.csv");
            fileError.createNewFile();

            FileWriter fileWriter = new FileWriter(fileError, true);
            for (int index: errorTable.keySet()) {
                ErrorRecord record = errorTable.get(index);
                fileWriter.write(sourceFile + ", " + record.getCode() + ", " + record.getMessage() + ", " + record.getLine() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Chyba");
        }

    }

}