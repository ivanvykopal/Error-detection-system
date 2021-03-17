package Backend.Controller;

import Backend.Main;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import Frontend.Analysis1Window;
import Frontend.Analysis2Window;
import Frontend.ErrorWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Analysis2Controller {
    File folder;

    @FXML
    private Label warning;

    @FXML
    public void getFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Vyberte priečinok");

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            warning.setTextFill(Color.web("#000000"));
            warning.setText("Priečinok: " + selectedDirectory.getAbsolutePath());
            folder = selectedDirectory;
        } else {
            warning.setTextFill(Color.web("#FF0000"));
            warning.setText("Chybný priečinok!");
            folder = null;
        }
    }

    public void analyzeCodes(ActionEvent event) throws IOException {
        ArrayList<String> fileNames = new ArrayList<>();
        deleteFiles();
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if (folder == null) {
            warning.setContentText("Nie je vybraný priečinok alebo vybraný priečinok je chybný!");
            warning.setHeaderText("Nesprávny priečinok!");
            warning.setTitle("Upozornenie");
            warning.show();
        }
        else {
            System.out.println("Analyzujem kódy!");
            File[] files = folder.listFiles();

            for (File file : files != null ? files : new File[0]) {
                if (file.isFile()) {
                    String name = file.toString();
                    if (!name.contains(".")) {
                        System.out.println("Súbor " + name + " nie je korektný!");
                        continue;
                    }
                    if (!name.substring(name.lastIndexOf('.') + 1).equals("c")) {
                        System.out.println("Súbor " + name + " nemá príponu .c!");
                        continue;
                    }
                    String text = null;
                    try {
                        text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Chyba!");
                    }

                    IncludePreprocessor prep = new IncludePreprocessor(text);
                    if (!prep.process()) {
                        System.out.println("Súbor " + file.getAbsolutePath() + " obsahuje aj študentom definované knižnice!");
                        continue;
                    }
                    System.out.println("Analyzujem súbor: " +  file.getAbsolutePath() + "!");
                    fileNames.add(file.getName());
                    ErrorDatabase errorDatabase = new ErrorDatabase();
                    Parser parser = new Parser(text, errorDatabase);
                    parser.parse(file.getName());
                    System.out.print("\n");
                    errorDatabase.getErrorMessages();
                    errorDatabase.createFile(file.getName());
                }
            }
            Stage stage = new Stage();
            Analysis2Window.closeStage();
            new ErrorWindow(stage, fileNames);
        }
    }

    private void deleteFiles() {
        File fileError = new File("errors.csv");
        fileError.delete();
        File fileVariables = new File("variables.csv");
        fileVariables.delete();
    }
}
