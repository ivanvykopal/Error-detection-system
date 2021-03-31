package Compiler.GraphColoring;

import Backend.ProgramLogger;
import Compiler.Errors.Error;
import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Kind;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Trieda pre kontrolu optimálneho využívania premenných v programe.
 *
 * @author Ivan Vykopal
 */
public class VariableUsageChecker {
    /** Atribút file predstavuje názov analyzovaného súboru. **/
    private String file;

    /**
     * Konštruktor, pre nastavenie názvu súboru.
     *
     * <p> V rámci konštruktora sa zároveň pre každú funkciu vytvorí zoznam premenných a matica susednosti.
     * Následne sa spustí farbenie grafov pre zistenie neoptimálnych premenných.
     *
     * @param symbolTable symbolická tabuľla
     *
     * @param errorDatabase databáza chýb
     *
     * @param file názov analyzovaného súboru
     */
    public VariableUsageChecker(SymbolTable symbolTable, ErrorDatabase errorDatabase, String file) {
        this.file = file;

        for (SymbolTable tab : symbolTable.getChilds()) {
            ArrayList<Index> indexes = findGlobal(symbolTable);
            ArrayList<Integer> list = new ArrayList<>();
            createIndexes(tab, list, indexes);
            byte[][] matrix = MatrixBuilder.createMatrix(tab, indexes);

            checkVariableUsage(indexes.size(), matrix, indexes, errorDatabase);
        }
    }

    /**
     * Metóda na vyhľadanie globálnych premenných v programe.
     *
     * @param symbolTable symbolická tabuľka
     *
     * @return zoznam globálnych premenných
     */
    private ArrayList<Index> findGlobal(SymbolTable symbolTable) {
        ArrayList<Index> indexes = new ArrayList<>();
        HashMap<String, Record> table = symbolTable.getTable();
        for (String key: table.keySet()) {
            Record record = table.get(key);
            if (record.getKind() == Kind.VARIABLE || record.getKind() == Kind.ARRAY) {
                Index index = new Index(key, record.getDeclarationLine(), record.getTypeString());
                index.setAccess(new ArrayList<>());
                index.setGlobal(true);
                indexes.add(index);
            }
        }
        return indexes;
    }

    /**
     * Metóda na vytvorenie zoznamu premenných, v takom poradí v akom budú v matici susednosti.
     *
     * @param symbolTable symbolická tabuľka
     *
     * @param list informácie pre prístup do správnej symbolickej tabuľky
     *
     * @param indexes zoznam premenných
     */
    private void createIndexes(SymbolTable symbolTable, ArrayList<Integer> list, ArrayList<Index> indexes) {
        for (String key : symbolTable.getTable().keySet()) {
            addItem(key, new ArrayList<>(list), indexes, symbolTable);
        }
        int i = 0;
        for (SymbolTable tab : symbolTable.getChilds()) {
            list.add(i);
            createIndexes(tab, list, indexes);
            list.remove(list.size() - 1);
            i++;
        }
    }

    /**
     * Metóda pre pridanie premennej do zoznamu premenných.
     *
     * @param key názov premennej
     *
     * @param list informácie pre prístup do správnej symbolickej tabuľky
     *
     * @param indexes zoznam premenných
     *
     * @param symbolTable symbolická tabuľka
     */
    private void addItem(String key, ArrayList<Integer> list, ArrayList<Index> indexes, SymbolTable symbolTable) {
        Record record = symbolTable.getTable().get(key);
        Index index = new Index(key, record.getDeclarationLine(), record.getTypeString());
        index.setAccess(list);
        index.setActiveLines(findActiveLines(record));
        /*StringBuilder strBuilder = new StringBuilder();
        for (int line: index.getActiveLines()) {
            strBuilder.append(line);
            strBuilder.append(", ");
        }
        System.out.println(key + " -> " + strBuilder.toString());*/
        indexes.add(index);
    }

    /**
     * Metóda na vyhľadanie aktívnych riadkov pre zadanú premennú. Aktívne riadky sa zisťujú na základe riadkov
     * inicializácií a využití.
     *
     * @param record zoznam zo symbolickej tabuľky pre premennú
     *
     * @return zoznam aktívnych riadkov pre premennú
     */
    private ArrayList<Integer> findActiveLines(Record record) {
        ArrayList<Integer> activeLines = new ArrayList<>();
        int initializationLength = record.getInitializationLines().size();
        int usageLength = record.getUsageLines().size();
        int i = 0, j = 0;
        int init, usage;
        if (usageLength == 0) {
            return activeLines;
        }
        //TODO: neviem, treba skontrolovať
        if (initializationLength == 0) {
            addActiveLines(record.getDeclarationLine(), record.getUsageLines().get(usageLength - 1), activeLines);
            return activeLines;
        }
        while (true) {
            if (i == initializationLength && j == usageLength) {
                break;
            }
            if (i == initializationLength) {
                i--;
            }
            if (j == usageLength) {
                j--;
            }
            init = record.getInitializationLines().get(i);
            usage = record.getUsageLines().get(j);
            if ((i + 1) < initializationLength && usage > record.getInitializationLines().get(i + 1)) {
                i++;
                continue;
            }
            if ((i + 1) >= initializationLength && (j + 1) < usageLength) {
                addActiveLines(init, record.getUsageLines().get(usageLength - 1), activeLines);
                break;
            } else if ((i + 1) < initializationLength && (j + 1) < usageLength &&
                    record.getInitializationLines().get(i + 1) >= record.getUsageLines().get(j + 1)) {
                j++;
            } else {
                addActiveLines(init, usage, activeLines);
                i++;
                j++;
            }
        }

        return activeLines;
    }

    /**
     * Metóda, ktorá pridá do zoznamu aktívnych riadkov, riadoky od hodnoty start po hodnotu end.
     *
     * @param start počiatočný riadok
     *
     * @param end koncový riadok
     *
     * @param activeLines zoznam aktívnych riadkov
     */
    private void addActiveLines(int start, int end, ArrayList<Integer> activeLines) {
        int length = activeLines.size();
        if (length != 0 && activeLines.get(length - 1) == start) {
            start++;
        }
        for (int i = start; i <= end; i++) {
            activeLines.add(i);
        }
    }

    /**
     * Metóda pre zistenie neoptimálneho využívania premenných.
     *
     * @param rows počet vrcholov
     *
     * @param matrix matica susednosti
     *
     * @param indexes zoznam premenných
     *
     * @param errorDatabase databáza chýb
     */
    private void checkVariableUsage(int rows, byte[][] matrix, ArrayList<Index> indexes, ErrorDatabase errorDatabase) {
        int[] colors = WelshPowellAlgorithm.graphColoring(rows, matrix);

        ArrayList<Integer>[] variables = new ArrayList[rows];
        for (int i = 0; i < rows; i++) {
            variables[i] = new ArrayList<>();
        }

        int maxColor = 0;
        for (int i = 0; i < rows; i++) {
            if ((colors[i] - 1) > maxColor) {
                maxColor = colors[i] - 1;
            }
            variables[colors[i] - 1].add(i);
        }

        if (maxColor != rows - 1 && maxColor != 0) {
            errorDatabase.addErrorMessage(0, Error.getError("E-RP-07"), "E-RP-07");
        }

        createVariables(maxColor, variables, indexes);
    }

    /**
     * Metóda na vytvorenie csv súboru s informáciami o možnosti zdieľania premenných.
     *
     * @param size maximálny možný počet premenných
     *
     * @param variables zoznam možnosti nahradenia premenných
     *
     * @param indexes zoznam premenných
     */
    private void createVariables(int size, ArrayList<Integer>[] variables, ArrayList<Index> indexes) {
        try {
            if (size == 0) {
                return;
            }
            File fileVariables = new File("variables.csv");
            fileVariables.createNewFile();

            FileWriter fileWriter = new FileWriter(fileVariables, true);
            for (int i = 0; i <= size; i++) {
                if (variables[i].size() > 1) {
                    StringBuilder strBuilder = new StringBuilder();
                    String prefix = "";
                    for (int index : variables[i]) {
                        strBuilder.append(prefix);
                        prefix = ", ";
                        strBuilder.append(indexes.get(index).getKey());
                        strBuilder.append(" (").append(indexes.get(index).getType());
                        strBuilder.append(", ").append(indexes.get(index).getDeclarationLine());
                        int length = indexes.get(index).getActiveLines().size();
                        strBuilder.append(" - ");
                         if (length > 0) {
                             strBuilder.append(indexes.get(index).getActiveLines().get(length - 1));
                         } else {
                             strBuilder.append(indexes.get(index).getDeclarationLine());
                         }
                        strBuilder.append(")");
                    }
                    fileWriter.write(file + ";" + strBuilder.toString() + "\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(VariableUsageChecker.class.getName()).log(Level.WARNING,
                    "Problém pri čítaní variables.csv!");
        }
    }
}
