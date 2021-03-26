package Backend.Controller;

import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Analysis2Controller extends Controller {
    File folder;

    @FXML
    private Label warning;

    @FXML
    private Button btnChooseFolder;

    @FXML
    private Button btnAnalyse;

    @FXML
    private Button btnMenu;

    @FXML
    public void goToMenu(ActionEvent event) throws IOException {
        showMainWindow();
    }

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
        //int fileCount = 0;
        if (folder == null) {
            warning.setContentText("Nie je vybraný priečinok alebo vybraný priečinok je chybný!");
            warning.setHeaderText("Nesprávny priečinok!");
            warning.setTitle("Upozornenie");
            warning.show();
        }
        else {
            btnAnalyse.setOnAction(null);
            btnChooseFolder.setOnAction(null);
            btnMenu.setOnAction(null);
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Informácia");
            info.setHeaderText("Analyzovanie programov");
            info.setContentText("Prebieha analýza programov, po jej ukončení sa zobrazí obrazovka s chybami.");
            info.show();

            System.out.println("Analyzujem kódy!");
            File[] files = folder.listFiles();

            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            int fileCount = 0;
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
                                    fileCount++;
                                    ErrorDatabase errorDatabase = new ErrorDatabase();
                                    Parser parser = new Parser(text, errorDatabase);
                                    parser.parse(file.getName());
                                    errorDatabase.createFile(file.getName());
                                    if (!errorDatabase.isEmpty()) {
                                        fileNames.add(file.getName());
                                    }
                                }
                            }

                            final CountDownLatch latch = new CountDownLatch(1);
                            int finalFileCount = fileCount;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        showErrorWindow(fileNames, finalFileCount);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        latch.countDown();
                                    }
                                }
                            });
                            latch.await();
                            return null;
                        }
                    };
                }
            };
            service.start();

            /*for (File file : files != null ? files : new File[0]) {
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
                    fileCount++;
                    ErrorDatabase errorDatabase = new ErrorDatabase();
                    Parser parser = new Parser(text, errorDatabase);
                    parser.parse(file.getName());
                    errorDatabase.createFile(file.getName());
                    if (!errorDatabase.isEmpty()) {
                        fileNames.add(file.getName());
                    }
                }
            }
            showErrorWindow(fileNames, fileCount);*/
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
