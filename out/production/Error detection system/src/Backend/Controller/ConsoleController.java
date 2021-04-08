package Backend.Controller;

import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    /**
     * Privátny konštruktor pre triedu {@code ConsoleController}
     */
    private ConsoleController() {}

    /**
     * Metóda pre
     */
    public static void runConsole() {
        while (true) {
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println("                                            Systém na detekciu chýb");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            System.out.println("Možnosti pre analyzovanie zdrojových kódov:");
            System.out.println("Pre analyzovanie jedného programu zadajte číslo 1");
            System.out.println("Pre analyzovanie viacerých programov zadajte číslo 2");
            System.out.println("Pre ukončenie programu zadajte číslo 3");
            System.out.print("\n");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Vaša možnosť: ");
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
                    System.out.println("Nesprávna možnosť!\n");
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
        System.out.print("Zadajte úplnú cestu k zdrojovému kódu: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        try {
            File fileAnalyzing = new File("unanalyzed_files.txt");
            deleteFiles();
            fileAnalyzing.createNewFile();
            FileWriter fileWriter = new FileWriter(fileAnalyzing, true);
            try {
                ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, "Analyzujem kód.");
                File file = new File(input);
                String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                IncludePreprocessor prep = new IncludePreprocessor(text);
                String lib = prep.process();
                if (!lib.equals("")) {
                    ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                            "Súbor " + input + " obsahuje nepodporovanú knižnicu: " + lib + "!");
                    fileWriter.write("Súbor " + input + " obsahuje nepodporovanú knižnicu: " + lib + "!\n");
                    return;
                }
                ErrorDatabase errorDatabase = new ErrorDatabase();
                Parser parser = new Parser(text, errorDatabase);
                parser.parse(file.getName());
                errorDatabase.createFile(file.getName());
                if (errorDatabase.isEmpty()) {
                    System.out.println("\nProgram " + file.getName() + " neobsahoval žiadnu chybu!\n");
                } else {
                    readErrorFile();
                    fillTableForAll(1);
                    System.out.println("\nVýsledky analýzy boli uložené v adresári " + new File("").getAbsolutePath() + "!");
                    System.out.println("Pri ďalšej analýze sú tieto výsledky premazané!\n");
                }
            } catch (IOException er) {
                ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                        "Vyskytla sa chyba pri práci s I/O súbormi!");
            } catch (Exception e) {
                ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                        "Vyskytla sa chyba spôsobená parserom!");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        System.out.print("\n");
        System.out.print("Zadajte úplnú cestu k adresáru so zdrojovými kódmi: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        try {
            File fileAnalyzing = new File("unanalyzed_files.txt");
            deleteFiles();
            fileAnalyzing.createNewFile();
            FileWriter fileWriter = new FileWriter(fileAnalyzing, true);

            System.out.println("Prebieha analyzovanie zdrojových kódov. Po ukončení analýzy sa zobrazí správa.");
            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO, "Analyzujem kódy.");
            File folder = new File(input);
            File[] files = folder.listFiles();
            int fileCount = 0;

            for (File file : files != null ? files : new File[0]) {
                if (file.isFile()) {
                    String name = file.toString();
                    if (!name.contains(".")) {
                        ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                "Súbor " + name + " nie je korektný.");
                        fileWriter.write("Súbor " + name + " nie je korektný.\n");
                        continue;
                    }
                    if (!name.substring(name.lastIndexOf('.') + 1).equals("c")) {
                        ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                "Súbor " + name + " nemá príponu .c!");
                        fileWriter.write("Súbor " + name + " nemá príponu .c!");
                        continue;
                    }
                    String text = null;
                    try {
                        text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    } catch (IOException e) {
                        ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.WARNING,
                                "Chyba pri načítaní zdrojového kódu!");
                        continue;
                    }

                    IncludePreprocessor prep = new IncludePreprocessor(text);
                    String lib = prep.process();
                    if (!lib.equals("")) {
                        ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                "Súbor " + file.getAbsolutePath() + " obsahuje nepodporovanú knižnicu: " + lib + "!");
                        fileWriter.write("Súbor " + file.getAbsolutePath() + " obsahuje nepodporovanú knižnicu: " + lib + "!");
                        continue;
                    }
                    ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                            "Analyzujem súbor: " + file.getAbsolutePath() + "!");
                    fileCount++;
                    try {
                        ErrorDatabase errorDatabase = new ErrorDatabase();
                        Parser parser = new Parser(text, errorDatabase);
                        parser.parse(file.getName());
                        errorDatabase.createFile(file.getName());
                    } catch (Exception e) {
                        ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.WARNING,
                                "Chyba pri analyzovaní súboru " + file.getAbsolutePath() + "!");
                    }
                }
            }
            readErrorFile();
            fillTableForAll(fileCount);
            System.out.println("\nVýsledky analýzy boli uložené v adresári " + new File("").getAbsolutePath() + "!");
            System.out.println("Pri ďalšej analýze sú tieto výsledky premazané!\n");

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metóda pre vytvorenie tabuľky so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     *
     * @param fileCount počet analyzovaných zdrojových kódov
     */
    private static void fillTableForAll(int fileCount) {
        HashMap<String, TableRecord> allCodesTable = new HashMap<>();
        int allErrorCount = 0;

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
            File fileVariables = new File("statistics.csv");
            fileVariables.createNewFile();

            FileWriter fileWriter = new FileWriter(fileVariables, true);
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
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    "Problém pri zápise do statisticscsv!");
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
            ProgramLogger.createLogger(ErrorController.class.getName()).log(Level.WARNING,
                    "Problém pri čítaní z errors.csv!");
        }
    }
}
