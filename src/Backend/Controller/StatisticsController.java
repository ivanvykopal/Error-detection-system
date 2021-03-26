package Backend.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class StatisticsController extends Controller {
    private HashMap<String, ArrayList<TableRecord>> table;
    private int allErrorCount = 0;
    private int fileCount = 0;
    @FXML
    ComboBox<String> comboBox;

    @FXML
    TableView<TableRecord> tableForOne;

    @FXML
    TableColumn<TableRecord, String> codeColumnOne;

    @FXML
    TableColumn<TableRecord, String> messageColumnOne;

    @FXML
    TableColumn<TableRecord, String> countOne;

    @FXML
    TableView<SummaryTableRecord> errorTablePercent;

    @FXML
    TableColumn<SummaryTableRecord, String> codeColumnPercent;

    @FXML
    TableColumn<SummaryTableRecord, String> messageColumnPercent;

    @FXML
    TableColumn<SummaryTableRecord, String> percentColumn;

    @FXML
    TableColumn<SummaryTableRecord, String> countColumn;

    @FXML
    Label meanErrorCount;

    @FXML
    public void goToMenu(ActionEvent event) throws IOException {
        showMainWindow();
    }

    @FXML
    public void getSelectedItem(ActionEvent event) {
        tableForOne.getItems().clear();
        if (!comboBox.getSelectionModel().getSelectedItem().equals("Vyberte súbor")) {
            fillTableForOne();
        } else {
            tableForOne.getItems().clear();
        }
    }

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

    private void fillTErrorTablePercent(HashMap<String, TableRecord> errorTable) {
        HashMap<String, Integer> fileErrorCount = findFileErrorCount();
        ArrayList<SummaryTableRecord> records = new ArrayList<>();
        try {
            File fileVariables = new File("error-total.csv");
            fileVariables.createNewFile();

            FileWriter fileWriter = new FileWriter(fileVariables, true);
            for (String key: errorTable.keySet()) {
                TableRecord record = errorTable.get(key);
                int count = fileErrorCount.get(key);
                BigDecimal percent = new BigDecimal((double) record.getNumber() / allErrorCount * 100)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal percentCount = new BigDecimal((double) count / fileCount * 100).setScale(2,
                        RoundingMode.HALF_UP);
                records.add(new SummaryTableRecord(record.getNumber() + " (" + percent + ")",
                        record.getMessage(), record.getCode(), count + " (" + percentCount + ")" ));
                fileWriter.write(record.getCode() + ", " + record.getNumber() + ", " + percent +  ", " + count
                        + ", " + percentCount + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<SummaryTableRecord> data2 = FXCollections.observableArrayList(records);
        errorTablePercent.setItems(data2);
    }

    public void fillTables() {
        fillTableForAll();
        fillMeanErrorCount();
    }

    public void fillComboBox(ArrayList<String> files) {
        comboBox.setItems(FXCollections.observableArrayList(files));
        comboBox.getSelectionModel().selectFirst();
    }

    private void fillMeanErrorCount() {
        meanErrorCount.setText(String.valueOf(allErrorCount / fileCount));
    }

    public void setTable(HashMap<String, ArrayList<TableRecord>> table) {
        this.table = table;
    }

    public void setFileCount(int count) {
        this.fileCount = count;
    }
}
