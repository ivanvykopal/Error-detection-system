package Backend.Controller;

import Compiler.Errors.ErrorRecord;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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


public class ErrorController extends Controller {
    private int fileCount = 1;
    ArrayList<String> files;
    private HashMap<String, ArrayList<TableRecord>> table = new HashMap<>();
    private HashMap<String, ArrayList<String>> table2 = new HashMap<>();

    @FXML
    ComboBox<String> comboBox;

    @FXML
    TableView<TableRecord> errorTable;

    @FXML
    TableView<String> variableTable;

    @FXML
    TableColumn<ErrorRecord, String> codeColumn;

    @FXML
    TableColumn<ErrorRecord, String> messageColumn;

    @FXML
    TableColumn<ErrorRecord, String> lineColumn;

    @FXML
    TableColumn<String, String> variableColumn;

    public ErrorController() {
        readErrorFile();
        readVariableFile();
    }

    @FXML
    public void getSelectedItem(ActionEvent event) {
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

    @FXML
    public void goToMenu(ActionEvent event) throws IOException {
        showMainWindow();
    }

    @FXML
    public void viewStatistics(ActionEvent event) throws IOException {
        showStatisticsWindow(table, fileCount, files);
    }

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
        } catch (FileNotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

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
        } catch (FileNotFoundException e) {
            System.out.println("Chyba v ErrorController!");
        }
    }

    private void setErrorTable() {
        if (!table.isEmpty()) {
            ArrayList<TableRecord> records = table.get(comboBox.getSelectionModel().getSelectedItem());
            ObservableList<TableRecord> data = FXCollections.observableArrayList(records);
            errorTable.setItems(data);
        }
    }

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

    public void fillComboBox(ArrayList<String> files) {
        this.files = files;
        comboBox.setItems(FXCollections.observableArrayList(files));
        comboBox.getSelectionModel().selectFirst();
    }

    public void setFileCount(int count) {
        this.fileCount = count;
    }
}
