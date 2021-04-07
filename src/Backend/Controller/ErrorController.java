package Backend.Controller;

import Backend.ProgramLogger;
import Compiler.Errors.ErrorRecord;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre ErrorController.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class ErrorController extends Controller {
    /** Atribút fileCount predstavuje počet analyzovaných zdrojových kódov. **/
    private int fileCount = 1;

    /** Atribút files predstavuje zoznam súborov, v ktorých sa nachádza aspoň jedna chyba. **/
    ArrayList<String> files;

    /** Atribút table predstavuje tabuľku s chybami pre jednotlivé zdrojové kódy. **/
    private HashMap<String, ArrayList<TableRecord>> table = new HashMap<>();

    /** Atribút table2 predstavuje tabuľky s možnosťami zdieľania premenných v jednotlivých zdrojových kódoch. **/
    private HashMap<String, ArrayList<String>> table2 = new HashMap<>();

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TableView<TableRecord> errorTable;

    @FXML
    private TableView<String> variableTable;

    @FXML
    private TableColumn<String, String> variableColumn;

    /**
     * Konštruktor, v ktorom sa načítavajú údaje do tabuľky s chybami a možnosti zdieľania premenných v jednotlivých
     * zdrojových kódov.
     */
    public ErrorController() {
        readErrorFile();
        readVariableFile();
    }

    /**
     * Metóda pre spracovanie výberu zdrojového kódu rozbaľovacieho poľa.
     *
     * <p> Po vybraní zdrojového kódu z rozbaľovacieho poľa sa zobrazia zistené chyby pre daný zdrojový kód.
     */
    @FXML
    public void getSelectedItem() {
        variableTable.getItems().clear();
        errorTable.getItems().clear();
        if (!comboBox.getSelectionModel().getSelectedItem().equals("Vyberte súbor")) {
            setErrorTable();
            setVariableTable();
        } else {
            variableTable.getItems().clear();
            errorTable.getItems().clear();
        }
    }

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
            ProgramLogger.createLogger(ErrorController.class.getName()).log(Level.WARNING,
                    "Problém pri načítaní showMainWindow()!");
        }
    }

    /**
     * Metóda pre spracovanie stlačenia tlačidla pre zobrazenie štatistík.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí obsazovka so štatistikami.
     */
    @FXML
    public void viewStatistics() {
        try {
            showStatisticsWindow(table, fileCount, files);
        } catch (IOException e) {
            ProgramLogger.createLogger(ErrorController.class.getName()).log(Level.WARNING,
                    "Problém pri načítaní showStatisticsWindow()!");
        }
    }

    /**
     * Metóda pre načítanie chýb pre jednotlivé zdrojové kódy z csv súboru.
     *
     * <p> V tejto metóde sa napĺňa atribút table.
     *
     */
    private void readErrorFile() {
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

    /**
     * Metóda pre načítanie možnosti zdieľania prememných pre jednotlivé zdrojové kódy.
     *
     * <p> V tejto metóde sa napĺňa atribút table2.
     *
     */
    private void readVariableFile() {
        try {
            File variableFile = new File("variables.csv");
            Scanner reader = new Scanner(variableFile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] line = data.split(";");
                if (line.length < 2) {
                    continue;
                }
                ArrayList<String> rows = table2.get(line[0]);
                if (rows == null) {
                    rows = new ArrayList<>();
                }
                rows.add(line[1].trim());
                table2.put(line[0], rows);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            ProgramLogger.createLogger(ErrorController.class.getName()).log(Level.WARNING,
                    "Problém pri čítaní z variables.csv!");
        }
    }

    /**
     * Metóda pre naplnenie tabuľky s chybami na obrazovke na základe vybraného zdrojového kódu.
     *
     */
    private void setErrorTable() {
        if (!table.isEmpty()) {
            ArrayList<TableRecord> records = table.get(comboBox.getSelectionModel().getSelectedItem());
            ObservableList<TableRecord> data = FXCollections.observableArrayList(records);
            errorTable.setItems(data);
        }
    }

    /**
     * Metóda pre naplnenie tabuľky s možnosťami zdieľania premenných na obrazovke pre vybraný zdrojový kód.
     *
     */
    private void setVariableTable() {
        if (!table2.isEmpty()) {
            ArrayList<String> records = table2.get(comboBox.getSelectionModel().getSelectedItem());
            ObservableList<String> data;
            if (records == null) {
                data = FXCollections.observableArrayList(new ArrayList<>());
            } else {
                data = FXCollections.observableArrayList(records);
            }
            variableColumn.setCellValueFactory(e -> new SimpleStringProperty((e.getValue())));
            variableTable.setItems(data);
        }
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
     * Metóda pre nastavenie počtu analyzovaných zdrojových kódov.
     *
     * @param count počet analyzovaných zdrojových kódov
     */
    public void setFileCount(int count) {
        this.fileCount = count;
    }
}
