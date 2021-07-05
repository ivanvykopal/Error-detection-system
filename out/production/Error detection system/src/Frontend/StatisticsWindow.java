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
    private JTable table1;
    private JScrollPane sp2;
    private JTable table2;
    private JLabel hide;
    private JLabel close;
    private JButton backBtn;
    private JButton menuBtn;
    private JPanel panel;

    public StatisticsWindow() {
        add(this.panel);
        this.setSize(1200, 800);
        setLocationRelativeTo(null);
        setUndecorated(true);

        sp1.setMaximumSize(new Dimension(300,250));
        sp1.setPreferredSize(new Dimension(300,250));

        sp2.setMaximumSize(new Dimension(300,250));
        sp2.setPreferredSize(new Dimension(300,250));

        String[] column_names = {"Kód chyby", "Chybová správa", "Číslo riadku"};
        DefaultTableModel table_model1 = new DefaultTableModel(column_names,0);
        table1.setModel(table_model1);
        table1.getColumnModel().getColumn(0).setMaxWidth(150);
        table1.getColumnModel().getColumn(0).setMaxWidth(150);
        table1.getColumnModel().getColumn(2).setPreferredWidth(100);
        table1.getColumnModel().getColumn(2).setMaxWidth(100);

        column_names = new String[]{"Kód chyby", "Chybová správa", "Početnosť chýb", "Početnosť súborov"};
        DefaultTableModel table_model2 = new DefaultTableModel(column_names,0);
        table2.setModel(table_model2);
        table2.getColumnModel().getColumn(0).setMaxWidth(150);
        table2.getColumnModel().getColumn(0).setMaxWidth(150);
        table2.getColumnModel().getColumn(2).setPreferredWidth(125);
        table2.getColumnModel().getColumn(2).setMaxWidth(125);
        table2.getColumnModel().getColumn(3).setPreferredWidth(125);
        table2.getColumnModel().getColumn(3).setMaxWidth(125);
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

    public void menuBtnAddListener(MouseListener listener) {
        menuBtn.addMouseListener(listener);
    }

    public void backBtnAddListener(MouseListener listener) {
        backBtn.addMouseListener(listener);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new StatisticsWindow();
        });
    }
}
