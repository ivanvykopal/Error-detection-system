package Backend.Controller;

import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import Frontend.Analysis2Window;
import Frontend.ErrorWindow;
import Frontend.MainWindow;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

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

    private final Analysis2Window window;

    /** Atribút folder predstavuje adresár s analyzovanými súbormi. **/
    private File folder;

    private Analysis2Controller(Analysis2Window window) {
        this.window = window;
        initController();
    }

    public static void createController(Analysis2Window window) {
        new Analysis2Controller(window);
    }

    private void initController() {
        this.window.analyzeBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }
        });

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

        this.window.homeAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                MainController.createController(new MainWindow());
                window.setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                window.getHome().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/home-1.png"))));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                window.getHome().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/home.png"))));
            }
        });

        this.window.analyzeBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                analyzeCodes();
            }
        });

        this.window.loadFolderBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                getFolder();
            }
        });
    }

        /**
     * Metóda pre spracovanie stlačenia výberu adresása so zdrojovými kódmi.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí okno pre výber adresáru.
     */
    public void getFolder() {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setDialogTitle("Vyberte Adresár");

        File selectedDirectory = null;
        int returnVal = directoryChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = directoryChooser.getSelectedFile();
        }

        if (selectedDirectory != null) {
            window.getWarning().setForeground(Color.BLACK);
            window.getWarning().setText("Adresár: " + selectedDirectory.getAbsolutePath());
            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                    "Adresár: " + selectedDirectory.getAbsolutePath() + " bol vybraný.");
            folder = selectedDirectory;
        } else {
            window.getWarning().setForeground(Color.RED);
            window.getWarning().setText("Chybný adresár!");
            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                    "Chybný adresár!");
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
    public void analyzeCodes() {
        ArrayList<String> fileNames = new ArrayList<>();
        deleteFiles();

        if (folder == null) {
            JOptionPane.showMessageDialog(null, "Nie je vybraný priečinok alebo vybraný priečinok je chybný!",
                    "Nesprávny adresár!", JOptionPane.WARNING_MESSAGE);

            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO, "Problém s adresárom!");
        }
        else {
            window.getLoadFolderBtn().setAction(null);
            window.getAnalyzeBtn().setAction(null);
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Informácia");
            info.setHeaderText("Analyzovanie programov");
            info.setContentText("Prebieha analýza programov, po jej ukončení sa zobrazí obrazovka s chybami.");
            info.show();

            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO, "Analyzujem kódy.");
            File[] files = folder.listFiles();

            try {
                Service<Void> service = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                File fileAnalyzing = new File("unanalyzed_files.txt");
                                fileAnalyzing.createNewFile();
                                FileWriter fileWriter = new FileWriter(fileAnalyzing, true);

                                int fileCount = 0;
                                for (File file : files != null ? files : new File[0]) {
                                    if (file.isFile()) {
                                        String name = file.toString();
                                        if (!name.contains(".")) {
                                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                                    "Súbor " + name + " nie je korektný.");
                                            fileWriter.write("Súbor " + name + " nie je korektný.\n");
                                            continue;
                                        }
                                        if (!name.substring(name.lastIndexOf('.') + 1).equals("c")) {
                                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                                    "Súbor " + name + " nemá príponu .c!");
                                            fileWriter.write("Súbor " + name + " nemá príponu .c!\n");
                                            continue;
                                        }
                                        String text = null;
                                        try {
                                            text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                                        } catch (IOException e) {
                                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.WARNING,
                                                    "Chyba pri načítaní zdrojového kódu!");
                                            continue;
                                        }

                                        IncludePreprocessor prep = new IncludePreprocessor(text);
                                        String lib = prep.process();
                                        if (!lib.equals("")) {
                                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                                    "Súbor " + file.getAbsolutePath() + " obsahuje nepodporovanú knižnicu: " + lib + "!");
                                            fileWriter.write("Súbor " + file.getAbsolutePath() + " obsahuje nepodporovanú knižnicu: " + lib + "!\n");
                                            continue;
                                        }
                                        ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                                "Analyzujem súbor: " + file.getAbsolutePath() + "!");
                                        fileCount++;
                                        try {
                                            ErrorDatabase errorDatabase = new ErrorDatabase();
                                            Parser parser = new Parser(text, errorDatabase);
                                            parser.parse(file.getName());
                                            errorDatabase.createFile(file.getName());
                                            if (!errorDatabase.isEmpty()) {
                                                fileNames.add(file.getName());
                                            }
                                        } catch (Exception e) {
                                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.WARNING,
                                                    "Chyba pri analyzovaní súboru " + file.getAbsolutePath() + "!");
                                            fileWriter.write("Chyba pri analyzovaní súboru " + file.getAbsolutePath() + "!\n");
                                        }
                                    }
                                }

                                final CountDownLatch latch = new CountDownLatch(1);
                                int finalFileCount = fileCount;
                                Platform.runLater(() -> {
                                    try {
                                        ErrorController.createController(new ErrorWindow(), fileNames, finalFileCount);
                                        window.setVisible(false);
                                    } finally {
                                        latch.countDown();
                                    }
                                });
                                latch.await();
                                fileWriter.close();
                                return null;
                            }
                        };
                    }
                };
                service.start();
            } catch (Exception e) {
                ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.WARNING,
                        "Problém pri analyzovaní zdrojových kódov!");
            }
        }
    }

}