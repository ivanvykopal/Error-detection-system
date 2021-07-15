package Backend.Controller;

import Backend.InternationalizationClass;
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
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre StatisticsWindow.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class StatisticsController extends Controller {

    private final StatisticsWindow window;

    /** Atribút table predstavuje tabuľky s chybami pre jednotlivé zdrojové kódy. **/
    private HashMap<String, ArrayList<TableRecord>> table;

    /** Atribút allErrorCount predstavuje celkový počet zistených chýb. **/
    private int allErrorCount = 0;

    /** Atribút fileCount predstavuje počet analyzovaných zdrojových kódov. **/
    private int fileCount = 0;

    /** Atribút files predstavuje zoznam súborov, v ktorých sa nachádza aspoň jedna chyba. **/
    private ArrayList<String> files;

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

    /**
     * Konštruktor, v ktorom sa načítavajú údaje do tabuľky s chybami a možnosti zdieľania premenných v jednotlivých
     * zdrojových kódov.
     */
    private StatisticsController(StatisticsWindow window, HashMap<String, ArrayList<TableRecord>> table, int count, ArrayList<String> files) {
        this.window = window;
        this.fileCount = count;
        this.table = table;

        fillTables();
        fillComboBox(files);
        initController();
    }

    public static void createController(StatisticsWindow window, HashMap<String, ArrayList<TableRecord>> table, int count, ArrayList<String> files) {
        new StatisticsController(window, table, count, files);
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

        this.window.getBack().setToolTipText(bundle.getString("back"));
        this.window.backAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                ErrorController.createController(new ErrorWindow(), files, fileCount);
                window.setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                window.getBack().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/back-arrow-1.png"))));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                window.getBack().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/back-arrow.png"))));
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
        DefaultTableModel model = (DefaultTableModel) window.getTableForOne().getModel();
        model.setRowCount(0);
        String selected = (String) window.getComboBox1().getSelectedItem();
        if (selected != null && !selected.equals(bundle.getString("chooseFile"))) {
            fillTableForOne();
        } else {
            model.setRowCount(0);
        }
    }

    /**
     * Metóda pre naplnenie tabuľky na obrazovke so súhrnnými počtami chýb pre vybraný zdrojový kód.
     */
    private void fillTableForOne() {
        HashMap<String, TableRecord> oneCodeTable = new HashMap<>();
        ArrayList<TableRecord> records = table.get((String) window.getComboBox1().getSelectedItem());

        for (TableRecord tbRecord: records) {
            TableRecord oneCodeRecord = oneCodeTable.get(tbRecord.getCode());
            if (oneCodeRecord == null) {
                oneCodeTable.put(tbRecord.getCode(), new TableRecord(1, tbRecord.getMessage(), tbRecord.getCode()));
            } else {
                oneCodeRecord.setNumber(oneCodeRecord.getNumber() + 1);
                oneCodeTable.put(tbRecord.getCode(), oneCodeRecord);
            }
        }

        DefaultTableModel model = (DefaultTableModel) window.getTableForOne().getModel();
        for (String key : oneCodeTable.keySet()) {
            Object[] row = new Object[3];
            TableRecord record = oneCodeTable.get(key);
            row[0] = record.getCode();
            row[1] = record.getMessage();
            row[2] = record.getNumber();
            model.addRow(row);
        }
    }

    /**
     * Metóda pre zistenie počtu súborov pre jednotlivé chyby, v ktorých sa dané chyby vyskytli.
     *
     * @return tabuľka s počtom súborov, pre jednotlivé chyby
     */
    private HashMap<String, Integer> findFileErrorCount() {
        HashMap<String, Integer> fileErrorCount = new HashMap<>();
        for (String key : table.keySet()) {
            ArrayList<TableRecord> records = table.get(key);
            Set<String> set = new HashSet<>();
            for (TableRecord record : records) {
                set.add(record.getCode());
            }

            for (String code : set) {
                Integer count = fileErrorCount.get(code);
                if (count == null) {
                    fileErrorCount.put(code, 1);
                } else {
                    fileErrorCount.replace(code, count + 1);
                }
            }
        }

        return fileErrorCount;
    }

    /**
     * Metóda pre vytvorenie tabuľky so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     */
    private void fillTableForAll() {
        HashMap<String, TableRecord> allCodesTable = new HashMap<>();

        for (String key : table.keySet()) {
            ArrayList<TableRecord> records = table.get(key);

            for (TableRecord tbRecord: records) {
                TableRecord oneCodeRecord = allCodesTable.get(tbRecord.getCode());

                allErrorCount++;
                if (oneCodeRecord == null) {
                    allCodesTable.put(tbRecord.getCode(), new TableRecord(1, tbRecord.getMessage(), tbRecord.getCode()));
                } else {
                    oneCodeRecord.setNumber(oneCodeRecord.getNumber() + 1);
                    allCodesTable.put(tbRecord.getCode(), oneCodeRecord);
                }
            }
        }

        fillTErrorTablePercent(allCodesTable);
    }

    /**
     * Metóda pre naplnenie tabuľky na obrazovke so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     *
     * @param errorTable tabuľka so súhrnnými informáciami o chybách pre všetky zdrojové kódy.
     */
    private void fillTErrorTablePercent(HashMap<String, TableRecord> errorTable) {
        HashMap<String, Integer> fileErrorCount = findFileErrorCount();
        ArrayList<SummaryTableRecord> records = new ArrayList<>();
        try {
            File fileStatistics = new File("total_statistics.csv");
            fileStatistics.createNewFile();

            FileWriter fileWriter = new FileWriter(fileStatistics, true);
            fileWriter.write(bundle.getString("totalHeader") + "\n");
            for (String key: errorTable.keySet()) {
                TableRecord record = errorTable.get(key);
                int count = fileErrorCount.get(key);
                BigDecimal percent = new BigDecimal((double) record.getNumber() / allErrorCount * 100)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal percentCount = new BigDecimal((double) count / fileCount * 100).setScale(2,
                        RoundingMode.HALF_UP);
                records.add(new SummaryTableRecord(record.getNumber() + " (" + percent + " %)",
                        record.getMessage(), record.getCode(), count + " (" + percentCount + " %)" ));
                fileWriter.write(record.getCode() + ", " + record.getNumber() + ", " + percent +  ", " + count
                        + ", " + percentCount + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    bundle.getString("totalErr"));
        }

        DefaultTableModel model = (DefaultTableModel) window.getErrorTablePercent().getModel();
        for(SummaryTableRecord record : records) {
            Object[] row = new Object[4];
            row[0] = record.getCode();
            row[1] = record.getMessage();
            row[2] = record.getRecord1();
            row[3] = record.getRecord2();
            model.addRow(row);
        }
    }

    /**
     * Metóda pre naplnenie tabuľky na obrazovke so súhrnnými informáciami o chybách pre všetky zdrojové kódy a naplnenie
     * informácie o priemernom počte chýb pre zdrojový kód.
     */
    public void fillTables() {
        fillTableForAll();
        fillMeanErrorCount();
        createStatisticsForOne();
    }

    /**
     * Metóda pre naplnenie rozbaľovacieho poľa so súbormi, v ktorých sa nachádza aspoň jedna chyba.
     *
     * @param files zoznam súborov, v ktorých sa nachádza aspoň jedna chyba
     */
    public void fillComboBox(ArrayList<String> files) {
        this.files = new ArrayList<>(files);
        files.add(0, bundle.getString("chooseFile"));

        window.getComboBox1().setModel(new DefaultComboBoxModel(files.toArray()));
        window.getComboBox1().setSelectedIndex(0);
    }

    /**
     * Metóda pre naplnenie informácie o priemernom počte chýb pre zdrojový kód.
     *
     */
    private void fillMeanErrorCount() {
        window.setText(String.valueOf(allErrorCount / fileCount));
    }

    /**
     * Metóda pre vytvorenie súboru so štatistikami pre jednotlivé zdojové kódy.
     */
    public void createStatisticsForOne() {
        HashMap<String, TableRecord> oneCodeTable;

        File fileStatistics = new File("program_statistics.csv");
        try {
            fileStatistics.createNewFile();

            FileWriter fileWriter = new FileWriter(fileStatistics, true);
            fileWriter.write(bundle.getString("programHeader") + "\n");
            ArrayList<String> keys = new ArrayList<>(table.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                oneCodeTable = new HashMap<>();
                ArrayList<TableRecord> records = table.get(key);

                for (TableRecord tbRecord: records) {
                    TableRecord oneCodeRecord = oneCodeTable.get(tbRecord.getCode());
                    if (oneCodeRecord == null) {
                        oneCodeTable.put(tbRecord.getCode(), new TableRecord(1, tbRecord.getMessage(), tbRecord.getCode()));
                    } else {
                        oneCodeRecord.setNumber(oneCodeRecord.getNumber() + 1);
                        oneCodeTable.put(tbRecord.getCode(), oneCodeRecord);
                    }
                }

                for (String keyValue : oneCodeTable.keySet()) {
                    TableRecord record = oneCodeTable.get(keyValue);
                    fileWriter.write(key + ", " + keyValue + ", " + record.getMessage() + ", " + record.getNumber() + "\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            ProgramLogger.createLogger(StatisticsController.class.getName()).log(Level.WARNING,
                    bundle.getString("programErr"));
        }
    }

}