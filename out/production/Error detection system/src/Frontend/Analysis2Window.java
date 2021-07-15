package Frontend;

import Backend.InternationalizationClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ResourceBundle;

public class Analysis2Window extends JFrame {
    private JButton loadFolderBtn;
    private JButton analyzeBtn;
    private JLabel close;
    private JPanel panel;
    private JLabel warning;
    private JLabel home;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private final ResourceBundle bundle = InternationalizationClass.getBundle();

    public Analysis2Window() {
        add(this.panel);
        this.setSize(800, 600);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setVisible(true);

        label1.setText(bundle.getString("title1"));
        label2.setText(bundle.getString("text4"));
        label3.setText(bundle.getString("text5"));
        label4.setText(bundle.getString("text6"));
        warning.setForeground(Color.RED);
        warning.setText(bundle.getString("warning2"));

        loadFolderBtn.setText(bundle.getString("loadFolderBtn"));
        analyzeBtn.setText(bundle.getString("analyzeBtn1"));

        addMouseMotionListener(new MouseMotionListener() {
            private int mx, my;

            @Override
            public void mouseMoved(MouseEvent e) {
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = Analysis2Window.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                Analysis2Window.this.setLocation(p);
            }
        });

    }

    public void loadFolderBtnAddListener(MouseListener listener) {
        loadFolderBtn.addMouseListener(listener);
    }

    public JButton getLoadFolderBtn() {
        return loadFolderBtn;
    }

    public void analyzeBtnAddListener(MouseListener listener) {
        analyzeBtn.addMouseListener(listener);
    }

    public JButton getAnalyzeBtn() {
        return analyzeBtn;
    }

    public void homeAddListener(MouseListener listener) {
        home.addMouseListener(listener);
    }

    public JLabel getHome() {
        return home;
    }

    public void closeAddListener(MouseListener listener) {
        close.addMouseListener(listener);
    }

    public JLabel getClose() {
        return close;
    }

    public JLabel getWarning() {
        return warning;
    }

}