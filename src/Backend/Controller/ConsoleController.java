package Backend.Controller;

import Backend.InternationalizationClass;
import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
/**
 * Trieda predstavujúca controller pre konzolovú verziu programu.
 *
 * <p> V rámci tejto triedy sa spracovávajú jednotlivé možnosti pre analýzu programov.
 *
 * @author Ivan Vykopal
 */
public final class ConsoleController extends Controller {

    /** Atribút table predstavuje tabuľku s chybami pre jednotlivé zdrojové kódy. **/
    private static HashMap<String, ArrayList<TableRecord>> table;

    /** **/
    private static int allErrorCount = 0;

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private static ResourceBundle bundle;

    /**
     * Privátny konštruktor pre triedu {@code ConsoleController}
     */
    private ConsoleController() {}

    /**
     * Metóda pre spustenie pprogramu v konzolovej verzii.
     */
    public static void runConsole() {
        selectLanguage();
        bundle = InternationalizationClass.getBundle();
        deleteLogFile();
        while (true) {
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println("                                            " + bundle.getString("systemName") );
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println(bundle.getString("options"));
            System.out.println(bundle.getString("option1"));
            System.out.println(bundle.getString("option2"));
            System.out.println(bundle.getString("option3"));
            System.out.print("\n");
            Scanner scanner = new Scanner(System.in);
            System.out.print(bundle.getString("option4") + " ");
            String input = scanner.nextLine().trim();

            switch(input) {
                case "1" :
                    analyzeCode();
                    break;
                case "2" :
                    analyzeCodes();
                    break;
                case "3":
                    System.exit(0);
                    break;
                default:
                    System.out.println(bundle.getString("optionErr") + "\n");
                    break;
            }
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static void selectLanguage() {
        boolean flag = false;
        while(!flag) {
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println("                                            Error detection system");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println("Select from the following languages:");
            System.out.println("For Slovak enter sk or SK");
            System.out.println("For English enter en or EN\n");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Your option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "sk":
                case "SK":
                    InternationalizationClass.setBundle("lang/bundle_sk", "lang/errors_sk", "sk", "SK");
                    flag = true;
                    break;
                case "en":
                case "EN":
                    InternationalizationClass.setBundle("lang/bundle_en", "lang/errors_en", "en", "US");
                    flag = true;
                    break;
                default:
                    System.out.println("Wrong option!\n");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    break;
            }
        }
    }

    /**
     * Metóda pre spracovanie možnosti analýzy jedného zdrojového kódu.
     *
     * <p> Po výbere možsnoti číslo 1 sa spustí analýza súboru. Pri analýze sa súbor načíta, predspracuje sa a následne
     * sa spustia kroky prekladu ako je lexikálna analýza, syntaktická analýza a sémantická analýza.
     *
     * <p> V rámci analyzovania súboru sa vyhodnocuje aj neoptímalne využívanie premenných na základe symbolickej tabuľky
     * a informácií v nej uložených.
     */
    private static void analyzeCode() {
        table = new HashMap<>();
        System.out.print("\n");
        System.out.print(bundle.getString("path") + " ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        try {
            File fileAnalyzing = new File("unanalyzed_files.txt");
            deleteFiles();
            fileAnalyzing.createNewFile();
            FileWriter fileWriter = new FileWriter(fileAnalyzing, true);
            try {
                File file = new File(input);
                if (!file.exists() || !file.isFile()) {
                    System.out.println(bundle.getString("pathErr"));
                    ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING, bundle.getString("fileErr4"));
                    return;
                }
                ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.INFO, bundle.getString("analyzing"));
                String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                IncludePreprocessor prep = new IncludePreprocessor(text);
                String lib = prep.process();
                if (!lib.equals("")) {
                    ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                            bundle.getString("file") + " " + input + bundle.getString("libraryErr") + " " + lib + "!");
                    fileWriter.write(bundle.getString("file") + " " + input + bundle.getString("libraryErr") + " " + lib + "!\n");
                    System.out.println(bundle.getString("file") + " " + input + bundle.getString("libraryErr") + " " + lib + "!");
                    return;
                }
                ErrorDatabase errorDatabase = new ErrorDatabase();
                Parser parser = new Parser(text, errorDatabase);
                parser.parse(file.getName());
                errorDatabase.createFile(file.getName());
                if (errorDatabase.isEmpty()) {
                    System.out.println("\n" + bundle.getString("program") + " " + file.getName() + " " + bundle.getString("noErr") + "\n");
                } else {
                    readErrorFile();
                    fillTableForAll(1);
                    createStatisticsForOne();
                    System.out.println("\n" + bundle.getString("results") + " " + new File("").getAbsolutePath() + "!");
                    System.out.println(bundle.getString("results1") + "\n");
                }
            } catch (IOException er) {
                ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                        bundle.getString("IOErr"));
            } catch (Exception e) {
                ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                        bundle.getString("parseErr"));
                fileWriter.write(bundle.getString("analyzingErr") + " " + input + "!\n");
            } finally {
                fileWriter.close();
            }
        } catch (IOException e) {
            ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                    bundle.getString("unanalyzedErr1"));
        }
    }

    /**
     * Metóda pre spracovanie možnosti analýzy viacerých zdrojových kódov.
     *
     * <p> Po výbere možnosti 2 sa spustí analýza zdrojových kódov. Pri analýze sa zdrojové kódy postupne načítajú,
     * predspracujú a následne sa spustia kroky prekladu, ako je lexikálna analýza, syntaktická analýza a sémantická
     * analýza.
     *
     * <p> V rámci analyzovania zdrojových kódov sa vyhodnocuje aj neoptímalne využívanie premenných na základe symbolickej
     * tabuľky a informácií v nej uložených.
     */
    private static void analyzeCodes() {
        table = new HashMap<>();
        allErrorCount = 0;
        System.out.print("\n");
        System.out.print(bundle.getString("path1") + " ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        try {
            File fileAnalyzing = new File("unanalyzed_files.txt");
            deleteFiles();
            fileAnalyzing.createNewFile();
            FileWriter fileWriter = new FileWriter(fileAnalyzing, true);

            File folder = new File(input);
            if (!folder.exists() || !folder.isDirectory()) {
                System.out.println(bundle.getString("pathErr1"));
                ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING, bundle.getString("directoryErr5"));
                return;
            }
            System.out.println(bundle.getString("analyzeInfo2"));
            ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.INFO, bundle.getString("analyzing4"));
            File[] files = folder.listFiles();
            int fileCount = 0;

            for (File file : files != null ? files : new File[0]) {
                if (file.isFile()) {
                    String name = file.toString();
                    if (!name.contains(".")) {
                        ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.INFO,
                                bundle.getString("file") + " " + name + " " + bundle.getString("incorrect"));
                        fileWriter.write(bundle.getString("file") + " " + name + " " + name + " " + bundle.getString("incorrect") + "\n");
                        continue;
                    }
                    if (!name.substring(name.lastIndexOf('.') + 1).equals("c")) {
                        ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.INFO,
                                bundle.getString("file") + " " + name + " " + bundle.getString("extensionErr") + " .c!");
                        fileWriter.write(bundle.getString("file") + " " + name + " " + bundle.getString("extensionErr") + " .c!\n");
                        continue;
                    }
                    String text = null;
                    try {
                        text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    } catch (IOException e) {
                        ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                                bundle.getString("readErr"));
                        continue;
                    }

                    IncludePreprocessor prep = new IncludePreprocessor(text);
                    String lib = prep.process();
                    if (!lib.equals("")) {
                        ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.INFO,
                                bundle.getString("file") + " " + file.getAbsolutePath() + " " + bundle.getString("libraryErr") + " " + lib + "!");
                        fileWriter.write(bundle.getString("file") + " " + file.getAbsolutePath() + " " + bundle.getString("libraryErr") + " " + lib + "!\n");
                        continue;
                    }
                    ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.INFO,
                            bundle.getString("analyzing2") + ": " + file.getAbsolutePath() + "!");
                    fileCount++;
                    try {
                        ErrorDatabase errorDatabase = new ErrorDatabase();
                        Parser parser = new Parser(text, errorDatabase);
                        parser.parse(file.getName());
                        errorDatabase.createFile(file.getName());
                    } catch (Exception e) {
                        ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                                bundle.getString("analyzingErr") + " " + file.getAbsolutePath() + "!");
                        fileWriter.write(bundle.getString("analyzingErr") + " " + file.getAbsolutePath() + "!\n");
                    }
                }
            }
            readErrorFile();
            fillTableForAll(fileCount);
            createStatisticsForOne();
            System.out.println("\n" + bundle.getString("text20") + " " + allErrorCount / fileCount);
            System.out.println("\n" + bundle.getString("results") + " " + new File("").getAbsolutePath() + "!");
            System.out.println(bundle.getString("results1") + "\n");

            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                    bundle.getString("unanalyzedErr1"));
        }
    }

    /**
     * Metóda pre vytvorenie tabuľky so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     *
     * @param fileCount počet analyzovaných zdrojových kódov
     */
    private static void fillTableForAll(int fileCount) {
        HashMap<String, TableRecord> allCodesTable = new HashMap<>();

        for (String key : table.keySet()) {
            ArrayList<TableRecord> records = table.get(key);

            for (TableRecord tbRecord: records) {
                TableRecord oneCodeRecord = allCodesTable.get(tbRecord.getCode());

                allErrorCount++;
                if (oneCodeRecord == null) {
                    allCodesTable.put(tbRecord.getCode(), new TableRecord(1, tbRecord.getMessage(), tbRecord.getCode()));
                } else {
                    oneCodeRecord.setNumber(oneCodeRecord.getNumber() + 1);
                    allCodesTable.put(tbRecord.getCode(), oneCodeRecord);
                }
            }
        }

        createStatistics(allCodesTable, allErrorCount, fileCount);
    }

    /**
     * Metóda pre naplnenie tabuľky na obrazovke so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     *
     * @param errorTable tabuľka so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     *
     * @param allErrorCount celkový počet chýb
     *
     * @param fileCount počet analyzovaných programov
     */
    private static void createStatistics(HashMap<String, TableRecord> errorTable, int allErrorCount, int fileCount) {
        HashMap<String, Integer> fileErrorCount = findFileErrorCount();
        try {
            File fileVariables = new File("total_statistics.csv");
            fileVariables.createNewFile();

            FileWriter fileWriter = new FileWriter(fileVariables, true);
            fileWriter.write(bundle.getString("totalHeader") + "\n");
            for (String key: errorTable.keySet()) {
                TableRecord record = errorTable.get(key);
                int count = fileErrorCount.get(key);
                BigDecimal percent = new BigDecimal((double) record.getNumber() / allErrorCount * 100)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal percentCount = new BigDecimal((double) count / fileCount * 100).setScale(2,
                        RoundingMode.HALF_UP);
                fileWriter.write(record.getCode() + ", " + record.getNumber() + ", " + percent +  ", " + count
                        + ", " + percentCount + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                    bundle.getString("totalErr"));
        }
    }

    /**
     * Metóda pre zistenie počtu súborov pre jednotlivé chyby, v ktorých sa dané chyby vyskytli.
     *
     * @return tabuľka s počtom súborov, pre jednotlivé chyby
     */
    private static HashMap<String, Integer> findFileErrorCount() {
        HashMap<String, Integer> fileErrorCount = new HashMap<>();
        for (String key : table.keySet()) {
            ArrayList<TableRecord> records = table.get(key);
            Set<String> set = new HashSet<>();
            for (TableRecord record : records) {
                set.add(record.getCode());
            }

            for (String code : set) {
                Integer count = fileErrorCount.get(code);
                if (count == null) {
                    fileErrorCount.put(code, 1);
                } else {
                    fileErrorCount.replace(code, count + 1);
                }
            }
        }

        return fileErrorCount;
    }

    /**
     * Metóda pre načítanie chýb pre jednotlivé zdrojové kódy z csv súboru.
     *
     * <p> V tejto metóde sa napĺňa atribút table.
     *
     */
    private static void readErrorFile() {
        try {
            File errorFile = new File("errors.csv");
            Scanner reader = new Scanner(errorFile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] line = data.split(",");
                if (line.length < 4) {
                    continue;
                }
                ArrayList<TableRecord> tableRecord = table.get(line[0]);
                if (tableRecord == null) {
                    tableRecord = new ArrayList<>();
                }
                tableRecord.add(new TableRecord(Integer.parseInt(line[3].trim()), line[2].trim(), line[1].trim()));
                table.put(line[0], tableRecord);
            }
            reader.close();
        } catch (FileNotFoundException | NumberFormatException e) {
            ProgramLogger.createLogger(ConsoleController.class.getName()).log(Level.WARNING,
                    bundle.getString("errorsErr"));
        }
    }

    /**
     * Metóda pre vymazanie súboru s logmi.
     */
    private static void deleteLogFile() {
        File fileError = new File("logs/log-file.log");
        fileError.delete();
    }

    /**
     * Metóda pre vytvorenie súboru so štatistikami pre jednotlivé zdojové kódy.
     */
    public static void createStatisticsForOne() {
        HashMap<String, TableRecord> oneCodeTable;

        File fileStatistics = new File("program_statistics.csv");
        try {
            fileStatistics.createNewFile();

            FileWriter fileWriter = new FileWriter(fileStatistics, true);
            fileWriter.write(bundle.getString("programHeader") + "\n");
            ArrayList<String> keys = new ArrayList<>(table.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                oneCodeTable = new HashMap<>();
                ArrayList<TableRecord> records = table.get(key);

                for (TableRecord tbRecord: records) {
                    TableRecord oneCodeRecord = oneCodeTable.get(tbRecord.getCode());
                    if (oneCodeRecord == null) {
                        oneCodeTable.put(tbRecord.getCode(), new TableRecord(1, tbRecord.getMessage(), tbRecord.getCode()));
                    } else {
                        oneCodeRecord.setNumber(oneCodeRecord.getNumber() + 1);
                        oneCodeTable.put(tbRecord.getCode(), oneCodeRecord);
                    }
                }

                for (String keyValue : oneCodeTable.keySet()) {
                    TableRecord record = oneCodeTable.get(keyValue);
                    fileWriter.write(key + ", " + keyValue + ", " + record.getMessage() + ", " + record.getNumber() +"\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    bundle.getString("programErr"));
        }

    }
}