package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Analysis2Window extends JFrame {
    private JButton loadFolderBtn;
    private JButton analyzeBtn;
    private JLabel close;
    private JLabel hide;
    private JPanel panel;
    private JLabel warning;
    private JLabel home;

    public Analysis2Window() {
        add(this.panel);
        this.setSize(800, 600);
        setLocationRelativeTo(null);
        setUndecorated(true);
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

    public void hideAddListener(MouseListener listener) {
        hide.addMouseListener(listener);
    }

    public JLabel getHide() {
        return hide;
    }

    public JLabel getWarning() {
        return warning;
    }

}