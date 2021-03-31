package Backend.Controller;

import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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

/**
 * Trieda predstavujúca controller pre Analysis2Window.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class Analysis2Controller extends Controller {
    /** Atribút folder predstavuje adresár s analyzovanými súbormi. **/
    private File folder;

    @FXML
    private Label warning;

    @FXML
    private Button btnChooseFolder;

    @FXML
    private Button btnAnalyse;

    @FXML
    private Button btnMenu;

    /**
     * Metóda pre spracovanie stlačenia tlačidla Menu.
     *
     * <p> Po stlačení tlačidla Menu sa zobrazí hlavné okno.
     *
     * @throws IOException
     */
    @FXML
    public void goToMenu() throws IOException {
        showMainWindow();
    }

    /**
     * Metóda pre spracovanie stlačenia výberu adresása so zdrojovými kódmi.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí okno pre výber adresáru.
     */
    @FXML
    public void getFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Vyberte priečinok");

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            warning.setTextFill(Color.web("#000000"));
            warning.setText("Adresár: " + selectedDirectory.getAbsolutePath());
            folder = selectedDirectory;
        } else {
            warning.setTextFill(Color.web("#FF0000"));
            warning.setText("Chybný priečinok!");
            folder = null;
        }
    }

    /**
     * Metóda pre spracovanie tlačidla spracovania zdrojových kódov.
     *
     * <p> Po stlačení daného tlačidla sa spustí analýza zdrojových kódov. Pri analýze sa zdrojové kódy postupne načítajú,
     * predspracujú a následne sa spustia kroky prekladu, ako je lexikálna analýza, syntaktická analýza a sémantická
     * analýza.
     *
     * <p> V rámci analyzovania zdrojových kódov sa vyhodnocuje aj neoptímalne využívanie premenných na základe symbolickej
     * tabuľky a informácií v nej uložených.
     */
    @FXML
    public void analyzeCodes() {
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
                                        //e.printStackTrace();
                                        System.out.println("Chyba pri načítaní zdrojového kódu!");
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
                                    try {
                                        parser.parse(file.getName());
                                    } catch (Exception e) {
                                        System.out.println("Chyba pri analyzovaní súboru " + file.getAbsolutePath() + "!");
                                    }
                                    errorDatabase.createFile(file.getName());
                                    if (!errorDatabase.isEmpty()) {
                                        fileNames.add(file.getName());
                                    }
                                }
                            }

                            final CountDownLatch latch = new CountDownLatch(1);
                            int finalFileCount = fileCount;
                            Platform.runLater(() -> {
                                try {
                                    showErrorWindow(fileNames, finalFileCount);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    latch.countDown();
                                }
                            });
                            latch.await();
                            return null;
                        }
                    };
                }
            };
            service.start();

        }
    }

    /**
     * Metóda pre vymazanie vybraných súborov.
     */
    private void deleteFiles() {
        File fileError = new File("errors.csv");
        fileError.delete();
        File fileVariables = new File("variables.csv");
        fileVariables.delete();
        File fileErrorTotal = new File("error-total.csv");
        fileErrorTotal.delete();
    }

}
