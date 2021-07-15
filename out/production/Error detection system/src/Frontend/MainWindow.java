package Frontend;

import Backend.InternationalizationClass;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ResourceBundle;

public class MainWindow extends JFrame {
    private JPanel panel1;
    private JButton analysis1Btn;
    private JButton analysis2Btn;
    private JLabel close;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

    public MainWindow() {
        add(this.panel1);
        this.setSize(800,600);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setVisible(true);

        initializeLabels();

        analysis1Btn.setText(bundle.getString("analysis1Btn"));
        analysis2Btn.setText(bundle.getString("analysis2Btn"));

        addMouseMotionListener(new MouseMotionListener() {
            private int mx, my;

            @Override
            public void mouseMoved(MouseEvent e) {
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = MainWindow.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                MainWindow.this.setLocation(p);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                toFront();
            }
        });
    }

    public void analysis1BtnAddListener(MouseListener listener) {
        analysis1Btn.addMouseListener(listener);
    }

    public void analysis2BtnAddListener(MouseListener listener) {
        analysis2Btn.addMouseListener(listener);
    }

    public void closeAddListener(MouseListener listener) {
        close.addMouseListener(listener);
    }

    public JLabel getClose() {
        return close;
    }

    private void initializeLabels() {
        label1.setText(bundle.getString("systemName"));
        label2.setText(bundle.getString("text13"));
        label3.setText(bundle.getString("text14"));
        label4.setText(bundle.getString("subtitle3"));
    }

}