package Frontend;

import Backend.InternationalizationClass;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ResourceBundle;

public class ErrorWindow extends JFrame {
    private JLabel close;
    private JPanel panel;
    private JComboBox comboBox1;
    private JTable errorTable;
    private JTable variableTable;
    private JScrollPane sp1;
    private JScrollPane sp2;
    private JLabel home;
    private JLabel statistics;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

    public ErrorWindow() {
        add(this.panel);
        this.setSize(1200, 800);
        setLocationRelativeTo(null);
        setUndecorated(true);

        initializeLabels();

        sp1.setMaximumSize(new Dimension(300,250));
        sp1.setPreferredSize(new Dimension(300,250));

        sp2.setMaximumSize(new Dimension(300,250));
        sp2.setPreferredSize(new Dimension(300,250));

        comboBox1.setPreferredSize(new Dimension(150,25));

        String[] column_names = {bundle.getString("code"), bundle.getString("message"), bundle.getString("line")};
        DefaultTableModel table_model1 = new DefaultTableModel(column_names,0);
        errorTable.setModel(table_model1);
        errorTable.getColumnModel().getColumn(0).setMaxWidth(150);
        errorTable.getColumnModel().getColumn(0).setMaxWidth(150);
        errorTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        errorTable.getColumnModel().getColumn(2).setMaxWidth(100);

        column_names = new String[]{bundle.getString("sharedVariables")};
        DefaultTableModel table_model2 = new DefaultTableModel(column_names,0);
        variableTable.setModel(table_model2);
        setVisible(true);

        addMouseMotionListener(new MouseMotionListener() {
            private int mx, my;

            @Override
            public void mouseMoved(MouseEvent e) {
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = ErrorWindow.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                ErrorWindow.this.setLocation(p);
            }
        });

    }

    public void homeAddListener(MouseListener listener) {
        home.addMouseListener(listener);
    }

    public JLabel getHome() {
        return home;
    }

    public void statisticsAddListener(MouseListener listener) {
        statistics.addMouseListener(listener);
    }

    public JLabel getStatistics() {
        return statistics;
    }

    public void closeAddListener(MouseListener listener) {
        close.addMouseListener(listener);
    }

    public JLabel getClose() {
        return close;
    }

    public JComboBox getComboBox1() {
        return comboBox1;
    }

    public JTable getErrorTable() {
        return errorTable;
    }

    public JTable getVariableTable() {
        return variableTable;
    }

    private void initializeLabels() {
        label1.setText(bundle.getString("title2"));
        label2.setText(bundle.getString("text7"));
        label3.setText(bundle.getString("text8"));
        label4.setText(bundle.getString("text9"));
        label5.setText(bundle.getString("text10"));
        label6.setText(bundle.getString("text11"));
        label7.setText(bundle.getString("subtitle1"));
        label8.setText(bundle.getString("text12"));
        label9.setText(bundle.getString("subtitle2"));
    }
}