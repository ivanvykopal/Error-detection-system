package Compiler.Errors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Trieda predstavujúca databázu chýb.
 * Pre každý riadok sa zapamätá len jedna chyba.
 *
 * @author Ivan Vykopal
 */
public final class ErrorDatabase implements Cloneable {

    /**
     * Atribút errorTable predstavuje hash tabuľku chýb, kde kľúč predstavuje riadok chyby a hodnota predstavuje
     * informácie o danej chybe.
     **/
    private HashMap<Integer, ErrorRecord> errorTable = new HashMap<>();

    /**
     * Metóda pre vytvorenie nového záznamu chyby do tabuľky chýb.
     *
     * @param line riadok chyby
     *
     * @param message chybová správa
     *
     * @param code kód chyby
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

    /**
     * Metóda pre vytvorenie kópie databázy chýb.
     *
     * @return kópiu databázy chýb, inak null
     */
    public ErrorDatabase createCopy() {
        try {
            return (ErrorDatabase) super.clone();
        } catch (CloneNotSupportedException e) {
            //e.printStackTrace();
            System.out.println("Chyba pri vytváraní kópie databázy chýb!");
        }
        return null;
    }

    /**
     * Metóda na zistenie, či databáza chýb je prázdna.
     *
     * @return true, v prípade, ak je databáza chýb prázdna
     *         false, v prípade, ak nie je databáza chýb prázdna
     */
    public boolean isEmpty() {
        return errorTable.isEmpty();
    }

    /**
     * Metóda pre vytvorenie csv súboru, respektíve pridávanie chýb do súboru "error.csv".
     * 
     * @param sourceFile názov analyzovaného súboru
     */
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
            System.out.println("Chyba pri vytváraní súboru errors.csv!");
        }

    }

}