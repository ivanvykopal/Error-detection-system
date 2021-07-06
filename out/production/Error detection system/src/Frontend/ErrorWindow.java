package Frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ErrorWindow extends JFrame {
    private JLabel close;
    private JLabel hide;
    private JPanel panel;
    private JComboBox comboBox1;
    private JTable table1;
    private JTable table2;
    private JScrollPane sp1;
    private JScrollPane sp2;
    private JButton menuBtn;
    private JButton statisticsBtn;

    public ErrorWindow() {
        add(this.panel);
        this.setSize(1200, 800);
        setLocationRelativeTo(null);
        setUndecorated(true);

        sp1.setMaximumSize(new Dimension(300,250));
        sp1.setPreferredSize(new Dimension(300,250));

        sp2.setMaximumSize(new Dimension(300,250));
        sp2.setPreferredSize(new Dimension(300,250));

        comboBox1.setPreferredSize(new Dimension(150,25));

        String[] column_names = {"Kód chyby","Chybová správa","Číslo riadku"};
        DefaultTableModel table_model1 = new DefaultTableModel(column_names,0);
        table1.setModel(table_model1);
        table1.getColumnModel().getColumn(0).setMaxWidth(150);
        table1.getColumnModel().getColumn(0).setMaxWidth(150);
        table1.getColumnModel().getColumn(2).setPreferredWidth(100);
        table1.getColumnModel().getColumn(2).setMaxWidth(100);

        column_names = new String[]{"Možné zdielané premenné"};
        DefaultTableModel table_model2 = new DefaultTableModel(column_names,0);
        table2.setModel(table_model2);
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

    public void menuBtnAddListener(MouseListener listener) {
        menuBtn.addMouseListener(listener);
    }

    public void statisticsBtnAddListener(MouseListener listener) {
        statisticsBtn.addMouseListener(listener);
    }

    public void closeAddListener(MouseListener listener) {
        close.addMouseListener(listener);
    }

    public JLabel getClose() {
        return close;
    }

    public void hideAddListener(MouseListener listener) {
        hide.addMouseListener(listener);
    }

    public JLabel getHide() {
        return hide;
    }
}