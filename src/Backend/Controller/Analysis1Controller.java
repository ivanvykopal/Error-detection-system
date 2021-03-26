package Backend.Controller;

import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class Analysis1Controller extends Controller {
    private String absolutePath;
    private String file;

    @FXML
    private Label warning;

    @FXML
    public void goToMenu(ActionEvent event) throws IOException {
        showMainWindow();
    }

    @FXML
    public void getFile(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Vyberte súbor");
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("C kód", "*.c"));

        File selectedFile = filechooser.showOpenDialog(null);

        if (selectedFile != null) {
            warning.setTextFill(Color.web("#000000"));
            warning.setText("Súbor: " + selectedFile.getAbsolutePath());
            absolutePath = selectedFile.getAbsolutePath();
            file = selectedFile.getName();
        } else {
            warning.setTextFill(Color.web("#FF0000"));
            warning.setText("Chybný súbor!");
            absolutePath = null;
        }
    }

    public void analyzeCode(ActionEvent event) {
        deleteFiles();
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if (absolutePath == null) {
            warning.setContentText("Nie je vybraný súbor alebo vybraný súbor je chybný!");
            warning.setHeaderText("Nesprávny súbor!");
            warning.setTitle("Upozornenie");
            warning.show();
        }
        else {
            System.out.println("Analyzujem kód!");
            try {
                //načíta súbor do reťazca
                String text = new String(Files.readAllBytes(Paths.get(absolutePath)));
                IncludePreprocessor prep = new IncludePreprocessor(text);
                if (!prep.process()) {
                    System.out.println("Súbor " + absolutePath + " obsahuje aj študentom definované knižnice!");
                    return;
                }
                ErrorDatabase errorDatabase = new ErrorDatabase();
                Parser parser = new Parser(text, errorDatabase);
                parser.parse(file);
                errorDatabase.createFile(file);
                if (errorDatabase.isEmpty()) {
                    showErrorWindow(new ArrayList<>());
                } else {
                    showErrorWindow(new ArrayList<>(Collections.singletonList(file)));
                }
            } catch (IOException er) {
                er.printStackTrace();
                System.out.println("Chyba v Analysis1Controller!");
            }

        }
    }

    private void deleteFiles() {
        File fileError = new File("errors.csv");
        fileError.delete();
        File fileVariables = new File("variables.csv");
        fileVariables.delete();
        File fileErrorTotal = new File("error-total.csv");
        fileErrorTotal.delete();
    }

}
