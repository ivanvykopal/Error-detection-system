package Backend.Controller;

import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
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
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre Analysis1Window.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class Analysis1Controller extends Controller {
    /** Atribút absolutePath obsahuje absolútnu cestu k analyzovanému súboru. **/
    private String absolutePath;

    /** Atribút file predstavuje analyzovaný súbor v textovej podobe. **/
    private String file;

    @FXML
    private Label warning;

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
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                    "Problém pri načítaní showMainWindow()!");
        }
    }

    /**
     * Metóda pre spracovanie stlačenia výberu súboru.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí okno pre výber súboru.
     */
    @FXML
    public void getFile() {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Vyberte súbor");
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("C kód", "*.c"));

        File selectedFile = filechooser.showOpenDialog(null);

        if (selectedFile != null) {
            warning.setTextFill(Color.web("#000000"));
            warning.setText("Súbor: " + selectedFile.getAbsolutePath());
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, "Súbor: " +
                    selectedFile.getAbsolutePath() + " bol vybraný.");
            absolutePath = selectedFile.getAbsolutePath();
            file = selectedFile.getName();
        } else {
            warning.setTextFill(Color.web("#FF0000"));
            warning.setText("Chybný súbor!");
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, "Chybný súbor!");
            absolutePath = null;
        }
    }

    /**
     * Metóda pre spracovanie tlačidla spracovania súboru.
     *
     * <p> Po stlačení daného tlačidla sa spustí analýza súboru. Pri analýze sa súbor načíta, predspracuje sa a následne
     * sa spustia kroky prekladu ako je lexikálna analýza, syntaktická analýza a sémantická analýza.
     *
     * <p> V rámci analyzovania súboru sa vyhodnocuje aj neoptímalne využívanie premenných na základe symbolickej tabuľky
     * a informácií v nej uložených.
     */
    @FXML
    public void analyzeCode() {
        deleteFiles();
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if (absolutePath == null) {
            warning.setContentText("Nie je vybraný súbor alebo vybraný súbor je chybný!");
            warning.setHeaderText("Nesprávny súbor!");
            warning.setTitle("Upozornenie");
            warning.show();
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, "Problém so súborom!");
        }
        else {
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, "Analyzujem kód.");
            try {
                //načíta súbor do reťazca
                String text = new String(Files.readAllBytes(Paths.get(absolutePath)));
                IncludePreprocessor prep = new IncludePreprocessor(text);
                if (!prep.process()) {
                    ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                            "Súbor " + absolutePath + " obsahuje aj študentom definované knižnice!");
                    return;
                }
                ErrorDatabase errorDatabase = new ErrorDatabase();
                Parser parser = new Parser(text, errorDatabase);
                parser.parse(file);
                errorDatabase.createFile(file);
                if (errorDatabase.isEmpty()) {
                    showErrorWindow(new ArrayList<>(), 1);
                } else {
                    showErrorWindow(new ArrayList<>(Collections.singletonList(file)), 1);
                }
            } catch (IOException er) {
                ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                        "Vyskytla sa chyba pri práci s I/O súbormi!");

            } catch (Exception e) {
                ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                        "Vyskytla sa chyba spôsobená parserom!");
            }

        }
    }

}
