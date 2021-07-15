package Backend.Controller;

import Backend.InternationalizationClass;
import Backend.ProgramLogger;
import Compiler.Errors.ErrorDatabase;
import Compiler.Parser.Parser;
import Compiler.Preprocessing.IncludePreprocessor;
import Frontend.Analysis2Window;
import Frontend.ErrorWindow;
import Frontend.MainWindow;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
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

    private SwingWorker sw;

    /** Atribút folder predstavuje adresár s analyzovanými súbormi. **/
    private File folder;

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

    private boolean analyzing = false;

    private boolean cancelled = false;

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

        this.window.getClose().setToolTipText(bundle.getString("close"));
        this.window.closeAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (analyzing) {
                    int input = JOptionPane.showConfirmDialog(null, bundle.getString("exitWarning"),
                            bundle.getString("closeSystem"), JOptionPane.YES_NO_OPTION);
                    if(input == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
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

        this.window.getHome().setToolTipText(bundle.getString("home"));
        this.window.homeAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (analyzing) {
                    int input = JOptionPane.showConfirmDialog(null, bundle.getString("homeWarning"),
                            bundle.getString("home"), JOptionPane.YES_NO_OPTION);
                    if(input == JOptionPane.YES_OPTION) {
                        cancelled = true;
                        sw.cancel(true);
                        MainController.createController(new MainWindow());
                        window.setVisible(false);
                    }
                } else {
                    MainController.createController(new MainWindow());
                    window.setVisible(false);
                }
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
        directoryChooser.setDialogTitle(bundle.getString("chooseDirectory"));

        File selectedDirectory = null;
        int returnVal = directoryChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = directoryChooser.getSelectedFile();
        }

        if (selectedDirectory != null) {
            window.getWarning().setForeground(Color.BLACK);
            String name = selectedDirectory.getAbsolutePath();
            if(name.length() > 30) {
                int index = name.substring(name.length() - 30).indexOf('\\') + name.length() - 30;
                window.getWarning().setText(bundle.getString("directory") + ": ..." + name.substring(index));
            } else {
                window.getWarning().setText(bundle.getString("directory") + ": " + name);
            }
            window.getWarning().setToolTipText(name);
            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                    bundle.getString("directory") + ": " + selectedDirectory.getAbsolutePath() + bundle.getString("choosed"));
            folder = selectedDirectory;
        } else {
            window.getWarning().setForeground(Color.RED);
            window.getWarning().setText(bundle.getString("directoryErr"));
            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                    bundle.getString("directoryErr"));
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
        deleteFiles();

        if (folder == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("directoryErr4"),
                    bundle.getString("directoryErr2"), JOptionPane.WARNING_MESSAGE);

            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO, bundle.getString("directoryErr3"));
        }
        else {
            for (MouseListener ml : window.getLoadFolderBtn().getMouseListeners()) {
                window.getLoadFolderBtn().removeMouseListener(ml);
            }

            for (MouseListener ml : window.getAnalyzeBtn().getMouseListeners()) {
                window.getAnalyzeBtn().removeMouseListener(ml);
            }
            JOptionPane.showMessageDialog(null, bundle.getString("analyzeInfo"),
                    bundle.getString("analyzing3"), JOptionPane.INFORMATION_MESSAGE);

            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO, bundle.getString("analyzing4"));
            File[] files = folder.listFiles();

            try {
                analyzeInBackground(files);
            } catch (Exception e) {
                ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.WARNING,
                        bundle.getString("analyzeErr"));
            }
        }
    }

    private void analyzeInBackground(File[] files) {
        sw = new SwingWorker() {
            ArrayList<String> fileNames = new ArrayList<>();
            int fileCount = 0;

            @Override
            protected Object doInBackground() throws Exception {
                analyzing = true;
                File fileAnalyzing = new File("unanalyzed_files.txt");
                fileAnalyzing.createNewFile();
                FileWriter fileWriter = new FileWriter(fileAnalyzing, true);

                for (File file : files != null ? files : new File[0]) {
                    if (file.isFile()) {
                        String name = file.toString();
                        if (!name.contains(".")) {
                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                    bundle.getString("file") + " " + name + bundle.getString("incorrect"));
                            fileWriter.write(bundle.getString("file") + " " + name + bundle.getString("incorrect") + "\n");
                            continue;
                        }
                        if (!name.substring(name.lastIndexOf('.') + 1).equals("c")) {
                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                    bundle.getString("file") + " " + name + " " + bundle.getString("extensionErr") + " .c!");
                            fileWriter.write(bundle.getString("file") + " " + name + " " + bundle.getString("extensionErr") + " .c!\n");
                            continue;
                        }
                        String text = null;
                        try {
                            text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                        } catch (IOException e) {
                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.WARNING,
                                    bundle.getString("readErr"));
                            continue;
                        }

                        IncludePreprocessor prep = new IncludePreprocessor(text);
                        String lib = prep.process();
                        if (!lib.equals("")) {
                            ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                    bundle.getString("file") + " " + file.getAbsolutePath() + bundle.getString("libraryErr") + lib + "!");
                            fileWriter.write(bundle.getString("file") + " " + file.getAbsolutePath() + bundle.getString("libraryErr") + lib + "!\n");
                            continue;
                        }
                        ProgramLogger.createLogger(Analysis2Controller.class.getName()).log(Level.INFO,
                                bundle.getString("analyzing2") + ": " + file.getAbsolutePath() + "!");
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
                                    bundle.getString("analyzingErr") + " " + file.getAbsolutePath() + "!");
                            fileWriter.write(bundle.getString("analyzingErr") + " " + file.getAbsolutePath() + "!\n");
                        }
                    }
                }

                fileWriter.close();
                return null;
            }

            @Override
            protected void done() {
                analyzing = false;
                if (!cancelled) {
                    ErrorController.createController(new ErrorWindow(), fileNames, fileCount);
                    window.setVisible(false);
                }
                cancelled = false;
            }
        };

        sw.execute();

    }

}