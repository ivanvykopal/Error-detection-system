package Frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class StatisticsWindow extends JFrame {
    private JComboBox comboBox1;
    private JScrollPane sp1;
    private JTable tableForOne;
    private JScrollPane sp2;
    private JTable errorTablePercent;
    private JLabel close;
    private JPanel panel;
    private JLabel back;
    private JLabel home;
    private JLabel meanErrorCount;

    public StatisticsWindow() {
        add(this.panel);
        this.setSize(1200, 800);
        setLocationRelativeTo(null);
        setUndecorated(true);

        sp1.setMaximumSize(new Dimension(300,250));
        sp1.setPreferredSize(new Dimension(300,250));

        sp2.setMaximumSize(new Dimension(300,250));
        sp2.setPreferredSize(new Dimension(300,250));

        String[] column_names = {"Kód chyby", "Chybová správa", "Počet výskytov"};
        DefaultTableModel table_model1 = new DefaultTableModel(column_names,0);
        tableForOne.setModel(table_model1);
        tableForOne.getColumnModel().getColumn(0).setMaxWidth(150);
        tableForOne.getColumnModel().getColumn(0).setMaxWidth(150);
        tableForOne.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableForOne.getColumnModel().getColumn(2).setMaxWidth(100);

        column_names = new String[]{"Kód chyby", "Chybová správa", "Početnosť chýb", "Početnosť súborov"};
        DefaultTableModel table_model2 = new DefaultTableModel(column_names,0);
        errorTablePercent.setModel(table_model2);
        errorTablePercent.getColumnModel().getColumn(0).setMaxWidth(150);
        errorTablePercent.getColumnModel().getColumn(0).setMaxWidth(150);
        errorTablePercent.getColumnModel().getColumn(2).setPreferredWidth(125);
        errorTablePercent.getColumnModel().getColumn(2).setMaxWidth(125);
        errorTablePercent.getColumnModel().getColumn(3).setPreferredWidth(125);
        errorTablePercent.getColumnModel().getColumn(3).setMaxWidth(125);
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
                Point p = StatisticsWindow.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                StatisticsWindow.this.setLocation(p);
            }
        });

    }

    public void homeAddListener(MouseListener listener) {
        home.addMouseListener(listener);
    }

    public JLabel getHome() {
        return home;
    }

    public void backAddListener(MouseListener listener) {
        back.addMouseListener(listener);
    }

    public JLabel getBack() {
        return back;
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

    public JTable getErrorTablePercent() {
        return errorTablePercent;
    }

    public JTable getTableForOne() {
        return tableForOne;
    }

    public void setText(String text) {
        meanErrorCount.setText("Priemerný počet chýb pre zdrojový kód: " + text);
    }
}