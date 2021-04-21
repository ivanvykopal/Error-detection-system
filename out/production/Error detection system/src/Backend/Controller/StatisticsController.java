package Backend.Controller;

import Backend.ProgramLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre StatisticsWindow.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class StatisticsController extends Controller {
    /** Atribút table predstavuje tabuľky s chybami pre jednotlivé zdrojové kódy. **/
    private HashMap<String, ArrayList<TableRecord>> table;

    /** Atribút allErrorCount predstavuje celkový počet zistených chýb. **/
    private int allErrorCount = 0;

    /** Atribút fileCount predstavuje počet analyzovaných zdrojových kódov. **/
    private int fileCount = 0;

    /** Atribút files predstavuje zoznam súborov, v ktorých sa nachádza aspoň jedna chyba. **/
    private ArrayList<String> files;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TableView<TableRecord> tableForOne;

    @FXML
    private TableView<SummaryTableRecord> errorTablePercent;

    @FXML
    private Label meanErrorCount;

    /**
     * Metóda pre spracovanie stlačenia tlačidla Menu.
     *
     * <p> Po stlačení tlačidla Menu sa zobrazí hlavné okno.
     */
    @FXML
    public void goToMenu() {
        try {
            showMainWindow();
        } catch (IOException e) {
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    "Problém pri načítaní showMainWindow()!");
        }
    }

    /**
     * Metóda pre spracovanie stlačenia tlačidla Späť.
     *
     * <p> Po stlačení tlačidla Späť sa zobrazí obrazovka so zoznamom chýb.
     */
    @FXML
    public void goBack() {
        try {
            showErrorWindow(files, fileCount);
        } catch (IOException e) {
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    "Problém pri načítaní showErrorWindow()!");
        }
    }

    /**
     * Metóda pre spracovanie výberu zdrojového kódu rozbaľovacieho poľa.
     *
     * <p> Po vybraní zdrojového kódu z rozbaľovacieho poľa sa zobrazia zistené chyby pre daný zdrojový kód.
     */
    @FXML
    public void getSelectedItem() {
        tableForOne.getItems().clear();
        if (!comboBox.getSelectionModel().getSelectedItem().equals("Vyberte súbor")) {
            fillTableForOne();
        } else {
            tableForOne.getItems().clear();
        }
    }

    /**
     * Metóda pre naplnenie tabuľky na obrazovke so súhrnnými počtami chýb pre vybraný zdrojový kód.
     */
    private void fillTableForOne() {
        HashMap<String, TableRecord> oneCodeTable = new HashMap<>();
        ArrayList<TableRecord> records = table.get(comboBox.getSelectionModel().getSelectedItem());

        for (TableRecord tbRecord: records) {
            TableRecord oneCodeRecord = oneCodeTable.get(tbRecord.getCode());
            if (oneCodeRecord == null) {
                oneCodeTable.put(tbRecord.getCode(), new TableRecord(1, tbRecord.getMessage(), tbRecord.getCode()));
            } else {
                oneCodeRecord.setNumber(oneCodeRecord.getNumber() + 1);
                oneCodeTable.put(tbRecord.getCode(), oneCodeRecord);
            }
        }

        records.clear();
        for (String key : oneCodeTable.keySet()) {
            records.add(oneCodeTable.get(key));
        }

        ObservableList<TableRecord> data = FXCollections.observableArrayList(records);
        tableForOne.setItems(data);
    }

    /**
     * Metóda pre zistenie počtu súborov pre jednotlivé chyby, v ktorých sa dané chyby vyskytli.
     *
     * @return tabuľka s počtom súborov, pre jednotlivé chyby
     */
    private HashMap<String, Integer> findFileErrorCount() {
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
     * Metóda pre vytvorenie tabuľky so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     */
    private void fillTableForAll() {
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

        fillTErrorTablePercent(allCodesTable);
    }

    /**
     * Metóda pre naplnenie tabuľky na obrazovke so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     *
     * @param errorTable tabuľka so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     */
    private void fillTErrorTablePercent(HashMap<String, TableRecord> errorTable) {
        HashMap<String, Integer> fileErrorCount = findFileErrorCount();
        ArrayList<SummaryTableRecord> records = new ArrayList<>();
        try {
            File fileStatistics = new File("total_statistics.csv");
            fileStatistics.createNewFile();

            FileWriter fileWriter = new FileWriter(fileStatistics, true);
            for (String key: errorTable.keySet()) {
                TableRecord record = errorTable.get(key);
                int count = fileErrorCount.get(key);
                BigDecimal percent = new BigDecimal((double) record.getNumber() / allErrorCount * 100)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal percentCount = new BigDecimal((double) count / fileCount * 100).setScale(2,
                        RoundingMode.HALF_UP);
                records.add(new SummaryTableRecord(record.getNumber() + " (" + percent + " %)",
                        record.getMessage(), record.getCode(), count + " (" + percentCount + " %)" ));
                fileWriter.write(record.getCode() + ", " + record.getNumber() + ", " + percent +  ", " + count
                        + ", " + percentCount + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    "Problém pri zápise do total_statistics.csv!");
        }
        ObservableList<SummaryTableRecord> data2 = FXCollections.observableArrayList(records);
        errorTablePercent.setItems(data2);
    }

    /**
     * Metóda pre naplnenie tabuľky na obrazovke so súhrnnými informáciami o chybách pre všetky zdrojové kódy a naplnenie
     * informácie o priemernom počte chýb pre zdrojový kód.
     */
    public void fillTables() {
        fillTableForAll();
        fillMeanErrorCount();
        createStatisticsForOne();
    }

    /**
     * Metóda pre naplnenie rozbaľovacieho poľa so súbormi, v ktorých sa nachádza aspoň jedna chyba.
     *
     * @param files zoznam súborov, v ktorých sa nachádza aspoň jedna chyba
     */
    public void fillComboBox(ArrayList<String> files) {
        this.files = new ArrayList<>(files);
        files.add(0, "Vyberte súbor");
        comboBox.setItems(FXCollections.observableArrayList(files));
        comboBox.getSelectionModel().selectFirst();
    }

    /**
     * Metóda pre naplnenie informácie o priemernom počte chýb pre zdrojový kód.
     *
     */
    private void fillMeanErrorCount() {
        meanErrorCount.setText(String.valueOf(allErrorCount / fileCount));
    }

    /**
     * Metóda pre nastavenie tabuľky s chybami pre jednotlivé zdrojové kódy.
     *
     * @param table tabuľka s chybami pre jednotlivé zdrojové kódy
     */
    public void setTable(HashMap<String, ArrayList<TableRecord>> table) {
        this.table = table;
    }

    /**
     * Metóda pre nastavenie počtu analyzovaných zdrojových kódov.
     *
     * @param count počet analyzovaných zdrojových kódov
     */
    public void setFileCount(int count) {
        this.fileCount = count;
    }

    /**
     * Metóda pre vytvorenie súboru so štatistikami pre jednotlivé zdojové kódy.
     */
    public void createStatisticsForOne() {
        HashMap<String, TableRecord> oneCodeTable;

        File fileStatistics = new File("program_statistics.csv");
        try {
            fileStatistics.createNewFile();

            FileWriter fileWriter = new FileWriter(fileStatistics, true);
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
                    fileWriter.write(key + ", " + keyValue + ", " + record.getMessage() + ", " + record.getNumber() + "\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    "Problém pri zápise do program_statistics.csv!");
        }
    }

}
