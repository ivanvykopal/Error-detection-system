package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Analysis1Window extends JFrame {

    private JPanel panel;
    private JButton loadFileBtn;
    private JLabel close;
    private JLabel hide;
    private JButton menuBtn;
    private JButton analyzeBtn;
    private JLabel warning;

    public Analysis1Window() {
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
                Point p = Analysis1Window.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                Analysis1Window.this.setLocation(p);
            }
        });

    }

    public void loadFileBtnAddListener(MouseListener listener) {
        loadFileBtn.addMouseListener(listener);
    }

    public void analyzeBtnAddListener(MouseListener listener) {
        analyzeBtn.addMouseListener(listener);
    }

    public void menuBtnAddListener(MouseListener listener) {
        menuBtn.addMouseListener(listener);
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