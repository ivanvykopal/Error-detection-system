package Compiler.GraphColoring;

import Backend.InternationalizationClass;
import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.SymbolTable.Record;
import Compiler.SymbolTable.SymbolTable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Trieda pre kontrolu optimálneho využívania premenných v programe.
 *
 * @author Ivan Vykopal
 */
public class VariableUsageChecker {
    /** Atribút file predstavuje názov analyzovaného súboru. **/
    private String file;

    /** Atribút parameters predstavuje zoznam parametrov funkcie. **/
    private ArrayList<String> mainParameters = new ArrayList<>();

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

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
        Record mainFunction = symbolTable.lookup("main");
        if (mainFunction != null) {
            mainParameters = mainFunction.getParameters();
        }

        for (SymbolTable tab : symbolTable.getChilds()) {
            ArrayList<Index> indexes = new ArrayList<>();
            ArrayList<Integer> list = new ArrayList<>();
            createIndexes(tab, list, indexes);
            byte[][] matrix = MatrixBuilder.createMatrix(tab, indexes);

            checkVariableUsage(indexes.size(), matrix, indexes, errorDatabase, tab);
        }
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
     *
     * @param table symbolická tabuľka
     */
    private void checkVariableUsage(int rows, byte[][] matrix, ArrayList<Index> indexes, ErrorDatabase errorDatabase, SymbolTable table) {
        int[] colors = WelshPowellAlgorithm.graphColoring(rows, matrix);

        ArrayList<Integer>[] variables = new ArrayList[rows];
        for (int i = 0; i < rows; i++) {
            variables[i] = new ArrayList<>();
        }

        int maxColor = -1;
        for (int i = 0; i < rows; i++) {
            if ((colors[i] - 1) > maxColor) {
                maxColor = colors[i] - 1;
            }
            variables[colors[i] - 1].add(i);
        }

        if (maxColor != rows - 1 && maxColor != -1) {
            findErrorType(maxColor, variables, indexes, errorDatabase, table);
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
            if (size == -1) {
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
                    bundle.getString("variablesErr2"));
        }
    }

    /**
     * Metóda pre zistenie, či ide o chybu neoptimálneho využívania premenných alebo neoptimálneho využívania argumentov
     * main funkcie.
     *
     * @param size maximálny možný počet premenných
     *
     * @param variables zoznam možnosti nahradenia premenných
     *
     * @param indexes zoznam premenných
     *
     * @param errorDatabase databáza chýb
     *
     * @param table symbolická tabuľka
     */
    private void findErrorType(int size, ArrayList<Integer>[] variables, ArrayList<Index> indexes, ErrorDatabase errorDatabase, SymbolTable table) {
        boolean isMain = false;
        for (String param : mainParameters) {
            if (table.getTable().containsKey(param)) {
                isMain = true;
            } else {
                isMain = false;
                break;
            }
        }
        if (!isMain) {
            errorDatabase.addErrorMessage(0, InternationalizationClass.getErrors().getString("E-RP-07"), "E-RP-07");
            return;
        }
        if (size == 0) {
            return;
        }
        boolean mainArgumentProblem = true;
        for (int i = 0; i <= size; i++) {
            if (variables[i].size() > 1) {
                int parameterCounts = 0;
                for (int index : variables[i]) {
                    if (mainParameters.contains(indexes.get(index).getKey())) {
                        parameterCounts++;
                    }
                }
                if (variables[i].size() - parameterCounts > 1) {
                    mainArgumentProblem = false;
                    break;
                }
            }
        }
        if (mainArgumentProblem) {
            errorDatabase.addErrorMessage(0, InternationalizationClass.getErrors().getString("E-RP-09"), "E-RP-09");
        } else {
            errorDatabase.addErrorMessage(0, InternationalizationClass.getErrors().getString("E-RP-07"), "E-RP-07");
        }
    }
}
