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

public class Analysis1Controller {
    String file;

    @FXML
    private Label warning;

    @FXML
    public void getFile(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Vyberte súbor");
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("C kód", "*.c"));

        File selectedFile = filechooser.showOpenDialog(null);

        if (selectedFile != null) {
            warning.setTextFill(Color.web("#000000"));
            warning.setText("Súbor: " + selectedFile.getAbsolutePath());
            file = selectedFile.getAbsolutePath();
        } else {
            warning.setTextFill(Color.web("#FF0000"));
            warning.setText("Chybný súbor!");
            file = null;
        }
    }

    public void analyzeCode(ActionEvent event) {
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if (file == null) {
            warning.setContentText("Nie je vybraný súbor alebo vybraný súbor je chybný!");
            warning.setHeaderText("Nesprávny súbor!");
            warning.setTitle("Upozornenie");
            warning.show();
        }
        else {
            System.out.println("Analyzujem kód!");
            try {
                //načíta súbor do reťazca
                String text = new String(Files.readAllBytes(Paths.get(file)));
                IncludePreprocessor prep = new IncludePreprocessor(text);
                if (!prep.process()) {
                    System.out.println("Súbor " + file + " obsahuje aj študentom definované knižnice!");
                    return;
                }
                ErrorDatabase errorDatabase = new ErrorDatabase();
                Parser parser = new Parser(text, errorDatabase);
                parser.parse();
                System.out.println("Koniec!");
                errorDatabase.getErrorMessages();
            } catch (IOException er) {
                er.printStackTrace();
                System.out.println("Chyba!");
            }

        }
    }
}
