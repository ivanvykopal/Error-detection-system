package Backend.Controller;

import Backend.InternationalizationClass;
import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import Frontend.Analysis1Window;
import Frontend.ErrorWindow;
import Frontend.MainWindow;
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
import java.util.ResourceBundle;
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

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

    private Analysis1Controller(Analysis1Window window) {
        this.window = window;

        initController();
    }

    public static void createController(Analysis1Window window) {
        new Analysis1Controller(window);
    }

    private void initController() {

        this.window.getClose().setToolTipText(bundle.getString("close"));
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

        this.window.getHome().setToolTipText(bundle.getString("home"));
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
    }

    /**
     * Metóda pre spracovanie stlačenia výberu súboru.
     *
     * <p> Po stlačení daného tlačidla sa zobrazí okno pre výber súboru.
     */
    public void getFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(bundle.getString("chooseFile"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(bundle.getString("cCode"), "c"));

        File selectedFile = null;
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }

        if (selectedFile != null) {
            window.getWarning().setForeground(Color.BLACK);
            String name = selectedFile.getAbsolutePath();
            if(name.length() > 30) {
                int index = name.substring(name.length() - 30).indexOf('\\') + name.length() - 30;
                window.getWarning().setText(bundle.getString("file") + ": ..." + name.substring(index));
            } else {
                window.getWarning().setText(bundle.getString("file") + ": " + name);
            }
            window.getWarning().setToolTipText(name);
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, bundle.getString("file") + ": " +
                    selectedFile.getAbsolutePath() + bundle.getString("choosed"));
            absolutePath = selectedFile.getAbsolutePath();
            file = selectedFile.getName();
        } else {
            window.getWarning().setForeground(Color.RED);
            window.getWarning().setText(bundle.getString("fileErr"));
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, bundle.getString("fileErr"));
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

            if (absolutePath == null) {
                JOptionPane.showMessageDialog(null, bundle.getString("fileErr1"),
                        bundle.getString("fileErr2"), JOptionPane.WARNING_MESSAGE);
                ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, bundle.getString("fileErr3"));
            }
            else {
                ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.INFO, bundle.getString("analyzing"));
                try {
                    //načíta súbor do reťazca
                    String text = new String(Files.readAllBytes(Paths.get(absolutePath)));
                    IncludePreprocessor prep = new IncludePreprocessor(text);
                    String lib = prep.process();
                    if (!lib.equals("")) {
                        ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                                bundle.getString("file") + " " + absolutePath + bundle.getString("libraryErr") + lib + "!");
                        fileWriter.write(bundle.getString("file") + " " + absolutePath + bundle.getString("libraryErr") + lib + "!\n");
                        JOptionPane.showMessageDialog(null, bundle.getString("file") + " " + absolutePath + bundle.getString("libraryErr") + lib + "!",
                                bundle.getString("analyzingErr") + "!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    ErrorDatabase errorDatabase = new ErrorDatabase();
                    Parser parser = new Parser(text, errorDatabase);
                    parser.parse(file);
                    errorDatabase.createFile(file);
                    if (errorDatabase.isEmpty()) {
                        ErrorController.createController(new ErrorWindow(), new ArrayList<>(), 1);
                    } else {
                        ErrorController.createController(new ErrorWindow(), new ArrayList<>(Collections.singletonList(file)),
                                1);
                    }
                    window.setVisible(false);
                } catch (IOException er) {
                    ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                            bundle.getString("IOErr"));
                    JOptionPane.showMessageDialog(null, bundle.getString("analyzingErr") + "!",
                            bundle.getString("analyzingErr") + "!", JOptionPane.WARNING_MESSAGE);

                } catch (Exception e) {
                    ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                            bundle.getString("parseErr"));
                    fileWriter.write(bundle.getString("analyzingErr") + " " + absolutePath + "!\n");
                    JOptionPane.showMessageDialog(null, bundle.getString("analyzingErr") + "!",
                            bundle.getString("analyzingErr") + "!", JOptionPane.WARNING_MESSAGE);

                }
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(Analysis1Controller.class.getName()).log(Level.WARNING,
                    bundle.getString("unanalyzedErr"));
        }
    }

}