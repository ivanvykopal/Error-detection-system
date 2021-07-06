package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MainWindow extends JFrame {
    private JPanel panel1;
    private JButton analysis1Btn;
    private JButton analysis2Btn;
    private JLabel close;

    public MainWindow() {
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

}