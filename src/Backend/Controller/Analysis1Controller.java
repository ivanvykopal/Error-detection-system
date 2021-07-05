package Backend.Controller;

import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import Frontend.Analysis1Window;
import Frontend.MainWindow;
import javafx.scene.control.Alert;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
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

    private final Analysis1Window window;

    /** Atribút absolutePath obsahuje absolútnu cestu k analyzovanému súboru. **/
    private String absolutePath;

    /** Atribút file predstavuje analyzovaný súbor v textovej podobe. **/
    private String file;

    private Analysis1Controller(Analysis1Window window) {
        this.window = window;

        this.window.closeAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                window.getClose().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/close-1.png"))));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                window.getClose().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/close.png"))));
            }
        });

        this.window.hideAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                window.setVisible(!window.isVisible());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                window.getHide().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/minus-1.png"))));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                window.getHide().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/minus.png"))));
            }
        });

        this.window.loadFileBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                getFile();
            }
        });

        this.window.analyzeBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                analyzeCode();
            }
        });

        this.window.menuBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                MainController.createController(new MainWindow());
                window.setVisible(false);
            }
        });
    }

    public static void createController(Analysis1Window window) {
        new Analysis1Controller(window);
    }


    /**
     * Metóda pre spracovanie stlačenia výberu súboru.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí okno pre výber súboru.
     */
    public void getFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Vyberte súbor");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("C kód", "c"));

        File selectedFile = null;
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }

        if (selectedFile != null) {
            window.getWarning().setForeground(Color.BLACK);
            window.getWarning().setText("Súbor: " + selectedFile.getAbsolutePath());
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, "Súbor: " +
                    selectedFile.getAbsolutePath() + " bol vybraný.");
            absolutePath = selectedFile.getAbsolutePath();
            file = selectedFile.getName();
        } else {
            window.getWarning().setForeground(Color.RED);
            window.getWarning().setText("Chybný súbor!");
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
    public void analyzeCode() {
        try {
            File fileAnalyzing = new File("unanalyzed_files.txt");
            deleteFiles();
            fileAnalyzing.createNewFile();
            FileWriter fileWriter = new FileWriter(fileAnalyzing, true);

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
                    String lib = prep.process();
                    if (!lib.equals("")) {
                        ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                                "Súbor " + absolutePath + " obsahuje nepodporovanú knižnicu: " + lib + "!");
                        fileWriter.write("Súbor " + absolutePath + " obsahuje nepodporovanú knižnicu: " + lib + "!\n");
                        warning.setContentText("Súbor " + absolutePath + " obsahuje nepodporovanú knižnicu: " + lib + "!");
                        warning.setHeaderText("Chyba pri analyzovaní súboru!");
                        warning.setTitle("Upozornenie");
                        warning.show();
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
                    warning.setContentText("Chyba pri analyzovaní súboru!");
                    warning.setHeaderText("Chyba pri analyzovaní súboru!");
                    warning.setTitle("Upozornenie");
                    warning.show();
                } catch (Exception e) {
                    ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                            "Vyskytla sa chyba spôsobená parserom!");
                    fileWriter.write("Chyba pri analyzovaní súboru " + absolutePath + "!\n");
                    warning.setContentText("Chyba pri analyzovaní súboru!");
                    warning.setHeaderText("Chyba pri analyzovaní súboru!");
                    warning.setTitle("Upozornenie");
                    warning.show();
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                    "Problém pri čítaní unanalyzed_files.txt");
        }
    }

}
