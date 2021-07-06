package Backend.Controller;

import Backend.ProgramLogger;
import Frontend.ErrorWindow;
import Frontend.MainWindow;
import Frontend.StatisticsWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre ErrorController.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class ErrorController extends Controller {

    private final ErrorWindow window;

    /** Atribút fileCount predstavuje počet analyzovaných zdrojových kódov. **/
    private int fileCount = 1;

    /** Atribút files predstavuje zoznam súborov, v ktorých sa nachádza aspoň jedna chyba. **/
    ArrayList<String> files;

    /** Atribút table predstavuje tabuľku s chybami pre jednotlivé zdrojové kódy. **/
    private HashMap<String, ArrayList<TableRecord>> table = new HashMap<>();

    /** Atribút table2 predstavuje tabuľky s možnosťami zdieľania premenných v jednotlivých zdrojových kódoch. **/
    private HashMap<String, ArrayList<String>> table2 = new HashMap<>();

    /**
     * Konštruktor, v ktorom sa načítavajú údaje do tabuľky s chybami a možnosti zdieľania premenných v jednotlivých
     * zdrojových kódov.
     */
    private ErrorController(ErrorWindow window, ArrayList<String> files, int fileCount) {
        this.window = window;
        this.fileCount = fileCount;

        fillComboBox(files);
        initController();

        readErrorFile();
        readVariableFile();
    }

    public static void createController(ErrorWindow window, ArrayList<String> files, int fileCount) {
        new ErrorController(window, files, fileCount);
    }

    private void initController() {
        this.window.getClose().setToolTipText("Ukončenie systému");
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

        this.window.getHome().setToolTipText("Návrat do menu");
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

        this.window.getStatistics().setToolTipText("Zobrazenie štatistík");
        this.window.statisticsAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                StatisticsController.createController(new StatisticsWindow(), table, fileCount, files);
                window.setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                window.getStatistics().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/statistics-1.png"))));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                window.getStatistics().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/statistics.png"))));
            }
        });

        this.window.getComboBox1().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                getSelectedItem();
            }
        });
    }

    /**
     * Metóda pre spracovanie výberu zdrojového kódu rozbaľovacieho poľa.
     *
     * <p> Po vybraní zdrojového kódu z rozbaľovacieho poľa sa zobrazia zistené chyby pre daný zdrojový kód.
     */
    public void getSelectedItem() {
        DefaultTableModel variableModel = (DefaultTableModel) window.getVariableTable().getModel();
        DefaultTableModel errorModel = (DefaultTableModel) window.getErrorTable().getModel();
        variableModel.setRowCount(0);
        errorModel.setRowCount(0);
        String selected = (String) window.getComboBox1().getSelectedItem();
        if (selected != null && !selected.equals("Vyberte súbor")) {
            setErrorTable();
            setVariableTable();
        } else {
            variableModel.setRowCount(0);
            errorModel.setRowCount(0);
        }
    }

    /**
     * Metóda pre načítanie chýb pre jednotlivé zdrojové kódy z csv súboru.
     *
     * <p> V tejto metóde sa napĺňa atribút table.
     *
     */
    private void readErrorFile() {
        try {
            File errorFile = new File("errors.csv");
            Scanner reader = new Scanner(errorFile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] line = data.split(",");
                if (line.length < 4) {
                    continue;
                }
                ArrayList<TableRecord> tableRecord = table.get(line[0]);
                if (tableRecord == null) {
                    tableRecord = new ArrayList<>();
                }
                tableRecord.add(new TableRecord(Integer.parseInt(line[3].trim()), line[2].trim(), line[1].trim()));
                table.put(line[0], tableRecord);
            }
            reader.close();
        } catch (FileNotFoundException | NumberFormatException e) {
            ProgramLogger.createLogger(ErrorController.class.getName()).log(Level.WARNING,
                    "Problém pri čítaní z errors.csv!");
        }
    }

    /**
     * Metóda pre načítanie možnosti zdieľania prememných pre jednotlivé zdrojové kódy.
     *
     * <p> V tejto metóde sa napĺňa atribút table2.
     *
     */
    private void readVariableFile() {
        try {
            File variableFile = new File("variables.csv");
            Scanner reader = new Scanner(variableFile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] line = data.split(";");
                if (line.length < 2) {
                    continue;
                }
                ArrayList<String> rows = table2.get(line[0]);
                if (rows == null) {
                    rows = new ArrayList<>();
                }
                rows.add(line[1].trim());
                table2.put(line[0], rows);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            ProgramLogger.createLogger(ErrorController.class.getName()).log(Level.WARNING,
                    "Problém pri čítaní z variables.csv!");
        }
    }

    /**
     * Metóda pre naplnenie tabuľky s chybami na obrazovke na základe vybraného zdrojového kódu.
     *
     */
    private void setErrorTable() {
        if (!table.isEmpty()) {
            ArrayList<TableRecord> records = table.get((String) window.getComboBox1().getSelectedItem());
            DefaultTableModel model = (DefaultTableModel) window.getErrorTable().getModel();
            for (TableRecord record : records) {
                Object[] row = new Object[3];
                row[0] = record.getCode();
                row[1] = record.getMessage();
                row[2] = record.getNumber();
                model.addRow(row);
            }
        }
    }

    /**
     * Metóda pre naplnenie tabuľky s možnosťami zdieľania premenných na obrazovke pre vybraný zdrojový kód.
     *
     */
    private void setVariableTable() {
        if (!table2.isEmpty()) {
            ArrayList<String> records = table2.get((String) window.getComboBox1().getSelectedItem());
            DefaultTableModel model = (DefaultTableModel) window.getVariableTable().getModel();

            if (records != null) {
                for (String item : records) {
                    model.addRow(new String[]{item});
                }
            }
        }
    }

    /**
     * Metóda pre naplnenie rozbaľovacieho poľa so súbormi, v ktorých sa nachádza aspoň jedna chyba.
     *
     * @param files zoznam súborov, v ktorých sa nachádza aspoň jedna chyba
     */
    public void fillComboBox(ArrayList<String> files) {
        this.files = new ArrayList<>(files);
        files.add(0, "Vyberte súbor");
        window.getComboBox1().setModel(new DefaultComboBoxModel(files.toArray()));
        window.getComboBox1().setSelectedIndex(0);
    }

}