package Frontend;

import Backend.Controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class LanguageWindow extends JFrame {
    private JLabel label4;
    private JLabel close;
    private JLabel label1;
    private JButton slovakBtn;
    private JButton englishBtn;
    private JPanel panel1;

    public LanguageWindow() {
        add(this.panel1);
        this.setSize(800,600);
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
                Point p = LanguageWindow.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                LanguageWindow.this.setLocation(p);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                toFront();
            }
        });
    }

    public void slovakBtnAddListener(MouseListener listener) {
        slovakBtn.addMouseListener(listener);
    }

    public void englishBtnAddListener(MouseListener listener) {
        englishBtn.addMouseListener(listener);
    }

    public void closeAddListener(MouseListener listener) {
        close.addMouseListener(listener);
    }

    public JLabel getClose() {
        return close;
    }

}